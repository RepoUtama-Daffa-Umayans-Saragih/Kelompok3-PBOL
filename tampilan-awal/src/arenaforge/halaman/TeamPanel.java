package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.MainFrame;
import arenaforge.Tema;
import arenaforge.model.AturanTurnamenException;
import arenaforge.model.Player;
import arenaforge.model.Team;
import arenaforge.ui.DaftarGelap;
import arenaforge.ui.Kartu;
import arenaforge.ui.Lencana;
import arenaforge.ui.Pesan;
import arenaforge.ui.TabelData;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Team & Roster (khusus panitia): daftar team di kiri, roster team yang dipilih di kanan.
 * Roster maksimal sesuai game (mis. 5 inti + 2 cadangan) dan terkunci kalau team ikut turnamen yang sudah berjalan.
 */
public class TeamPanel extends JPanel {
    private final MainFrame frame;
    private final DaftarGelap<Team> daftarTeam = new DaftarGelap<>(DataSementara.TEAM, Team::getNama,
            t -> t.getGame().getNama() + ", " + t.getRoster().size() + "/" + t.getGame().kapasitas() + " pemain");
    private final TabelData tabelRoster = new TabelData("Nickname", "Role", "Posisi", "ID").lebarKolom(0, 150).lebarKolom(1, 130);
    private final JLabel lblNama = Tema.label("Pilih team di daftar", Font.BOLD, 20f);
    private final JLabel lblInfo = Tema.redup("");
    private final JPanel wadahStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final Tombol btnHapusTeam = new Tombol("Hapus Team", Tombol.Jenis.KEDUA);
    private final Tombol btnTambah = new Tombol("Tambah Player", Tombol.Jenis.UTAMA);
    private final Tombol btnKeluarkan = new Tombol("Keluarkan Player", Tombol.Jenis.KEDUA);

    public TeamPanel(MainFrame frame) {
        super(new BorderLayout(0, 16));
        this.frame = frame;
        setBorder(Tema.padding(24, 28, 24, 28));

        Tombol btnBuat = new Tombol("Buat Team", Tombol.Jenis.UTAMA);
        btnBuat.addActionListener(e -> buatTeam());
        btnHapusTeam.addActionListener(e -> hapusTeam());
        btnTambah.addActionListener(e -> tambahPlayer());
        btnKeluarkan.addActionListener(e -> keluarkanPlayer());
        daftarTeam.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetail();
            }
        });
        tabelRoster.getSelectionModel().addListSelectionListener(e -> aturTombol());

        add(Kerangka.kepala("Team & Roster", "Buat team untuk satu game, lalu tambah atau keluarkan player dari rosternya.",
                Kerangka.barisTombol(btnHapusTeam, btnBuat)), BorderLayout.NORTH);

        JScrollPane gulirDaftar = Tema.gulir(daftarTeam);
        gulirDaftar.getViewport().setBackground(Tema.PERMUKAAN);
        gulirDaftar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Kartu kiri = Kerangka.kartu("Team", gulirDaftar);
        kiri.setPreferredSize(new Dimension(270, 0));

        wadahStatus.setOpaque(false);
        JPanel judul = new JPanel(new BorderLayout(0, 4));
        judul.setOpaque(false);
        judul.add(lblNama, BorderLayout.NORTH);
        judul.add(lblInfo, BorderLayout.CENTER);
        judul.add(wadahStatus, BorderLayout.SOUTH);

        JScrollPane gulirRoster = Tema.gulir(tabelRoster);
        gulirRoster.getViewport().setBackground(Tema.PERMUKAAN);
        JPanel tombolRoster = Kerangka.barisTombol(btnKeluarkan, btnTambah);

        Kartu kanan = new Kartu(new BorderLayout(0, 12));
        kanan.setBorder(Tema.padding(16, 18, 16, 18));
        kanan.add(judul, BorderLayout.NORTH);
        kanan.add(gulirRoster, BorderLayout.CENTER);
        kanan.add(tombolRoster, BorderLayout.SOUTH);

        JPanel isi = new JPanel(new BorderLayout(16, 0));
        isi.setOpaque(false);
        isi.add(kiri, BorderLayout.WEST);
        isi.add(kanan, BorderLayout.CENTER);
        add(isi, BorderLayout.CENTER);

        if (!DataSementara.TEAM.isEmpty()) {
            daftarTeam.setSelectedIndex(0);
        } else {
            tampilkanDetail();
        }
    }

    private void tampilkanDetail() {
        Team t = daftarTeam.getSelectedValue();
        tabelRoster.kosongkan();
        wadahStatus.removeAll();
        if (t == null) {
            lblNama.setText("Pilih team di daftar");
            lblInfo.setText(DataSementara.TEAM.isEmpty() ? "Belum ada team. Klik \"Buat Team\" untuk membuatnya." : " ");
        } else {
            lblNama.setText(t.getNama());
            lblInfo.setText(t.getId() + ", " + t.getGame().getNama() + ", roster " + t.getGame().deskripsiRoster()
                    + " (terisi " + t.getRoster().size() + ")");
            for (Player p : t.getRoster()) {
                tabelRoster.tambah(p.getNickname(), p.getRole(), t.isInti(p) ? "Inti" : "Cadangan", p.getId());
            }
            wadahStatus.add(t.isLengkap()
                    ? new Lencana("Roster lengkap", Lencana.Jenis.LOLOS)
                    : new Lencana("Roster belum lengkap", Lencana.Jenis.GRUP));
            if (t.isTerkunci()) {
                wadahStatus.add(new Lencana("Terkunci: ikut turnamen berjalan", Lencana.Jenis.GUGUR));
            }
        }
        wadahStatus.revalidate();
        wadahStatus.repaint();
        aturTombol();
    }

    private void aturTombol() {
        Team t = daftarTeam.getSelectedValue();
        btnHapusTeam.setEnabled(t != null);
        btnTambah.setEnabled(t != null);
        btnKeluarkan.setEnabled(t != null && tabelRoster.getSelectedRow() >= 0);
    }

    private void buatTeam() {
        Team baru = new TeamFormDialog(SwingUtilities.getWindowAncestor(this)).tampilkan();
        if (baru != null) {
            DataSementara.TEAM.addElement(baru);
            daftarTeam.setSelectedValue(baru, true);
            frame.catatAktivitas("Team " + baru.getNama() + " (" + baru.getId() + ") dibuat untuk " + baru.getGame().getNama());
        }
    }

    private void hapusTeam() {
        Team t = daftarTeam.getSelectedValue();
        if (t == null) {
            return;
        }
        if (!DataSementara.turnamenDiikuti(t).isEmpty()) {
            Pesan.peringatan(this, "Hapus Team", t.getNama() + " masih terdaftar di turnamen "
                    + DataSementara.turnamenDiikuti(t).get(0).getNama() + ". Keluarkan dulu dari turnamen.");
            return;
        }
        if (!Pesan.konfirmasi(this, "Hapus Team", "Hapus team " + t.getNama() + "? Pemainnya tetap ada dan bisa masuk team lain.")) {
            return;
        }
        try {
            t.bubarkan();
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Hapus Team", ex.getMessage());
            return;
        }
        DataSementara.TEAM.removeElement(t);
        frame.catatAktivitas("Team " + t.getNama() + " dihapus");
        tampilkanDetail();
    }

    private void tambahPlayer() {
        Team t = daftarTeam.getSelectedValue();
        if (t == null) {
            return;
        }
        List<Player> bebas = new ArrayList<>();
        for (int i = 0; i < DataSementara.PLAYER.size(); i++) {
            Player p = DataSementara.PLAYER.get(i);
            if (p.getTeam() == null && p.getGame() == t.getGame()) {
                bebas.add(p);
            }
        }
        if (bebas.isEmpty()) {
            Pesan.info(this, "Tambah Player", "Tidak ada player " + t.getGame().getNama()
                    + " yang belum punya team. Daftarkan player baru di menu Player.");
            return;
        }
        List<Player> dipilih = new PilihDialog<>(SwingUtilities.getWindowAncestor(this), "Tambah Player ke " + t.getNama(),
                "Pilih player " + t.getGame().getNama() + " yang belum punya team. Boleh lebih dari satu.",
                bebas, Player::getNickname, p -> p.getId() + ", " + p.getRole(), true).tampilkan();
        int masuk = 0;
        for (Player p : dipilih) {
            try {
                p.gabungTeam(t); // game, kapasitas roster, dan kunci diperiksa oleh Team
                masuk++;
            } catch (AturanTurnamenException ex) {
                Pesan.peringatan(this, "Tambah Player", ex.getMessage());
                break;
            }
        }
        if (masuk > 0) {
            frame.catatAktivitas(masuk + " player masuk roster " + t.getNama());
        }
        segarkan(t);
    }

    private void keluarkanPlayer() {
        Team t = daftarTeam.getSelectedValue();
        int baris = tabelRoster.getSelectedRow();
        if (t == null || baris < 0) {
            return;
        }
        Player p = t.getRoster().get(baris);
        if (!Pesan.konfirmasi(this, "Keluarkan Player", "Keluarkan " + p.getNickname() + " dari " + t.getNama() + "?")) {
            return;
        }
        try {
            t.hapusPlayer(p);
            frame.catatAktivitas(p.getNickname() + " dikeluarkan dari roster " + t.getNama());
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Keluarkan Player", ex.getMessage());
        }
        segarkan(t);
    }

    /** Menggambar ulang daftar (jumlah pemain berubah) dan detail team tanpa kehilangan pilihan. */
    private void segarkan(Team t) {
        daftarTeam.repaint();
        tampilkanDetail();
        daftarTeam.setSelectedValue(t, true);
    }
}
