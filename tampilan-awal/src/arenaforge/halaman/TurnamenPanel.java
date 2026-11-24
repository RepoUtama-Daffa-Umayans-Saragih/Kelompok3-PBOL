package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.MainFrame;
import arenaforge.Tema;
import arenaforge.model.AturanTurnamenException;
import arenaforge.model.Team;
import arenaforge.model.Tournament;
import arenaforge.ui.DaftarGelap;
import arenaforge.ui.Kartu;
import arenaforge.ui.Lencana;
import arenaforge.ui.Pesan;
import arenaforge.ui.TabelData;
import arenaforge.ui.TeksBungkus;
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
 * Turnamen (khusus panitia): membuat turnamen, mendaftarkan atau mengeluarkan team,
 * lalu menyusun jadwal (undian grup + bagan gugur) setelah kuota penuh.
 */
public class TurnamenPanel extends JPanel {
    private final MainFrame frame;
    private final DaftarGelap<Tournament> daftar = new DaftarGelap<>(DataSementara.TURNAMEN, Tournament::getNama,
            t -> t.getGame().getNama() + ", " + t.getPeserta().size() + "/" + t.getKuota() + " team, " + t.statusTeks());
    private final TabelData tabelPeserta = new TabelData("No", "Team", "Roster").ratakanTengah(0).lebarKolom(0, 50).lebarKolom(1, 220);
    private final JLabel lblNama = Tema.label("Pilih turnamen di daftar", Font.BOLD, 20f);
    private final JLabel lblInfo = Tema.redup("");
    private final JPanel wadahStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final Tombol btnHapusTurnamen = new Tombol("Hapus Turnamen", Tombol.Jenis.KEDUA);
    private final Tombol btnDaftarkan = new Tombol("Daftarkan Team", Tombol.Jenis.KEDUA);
    private final Tombol btnHapusPeserta = new Tombol("Hapus Peserta", Tombol.Jenis.KEDUA);
    private final Tombol btnJadwal = new Tombol("Susun Jadwal", Tombol.Jenis.UTAMA);
    private final Tombol btnBatalJadwal = new Tombol("Batalkan Jadwal", Tombol.Jenis.KEDUA);

    public TurnamenPanel(MainFrame frame) {
        super(new BorderLayout(0, 16));
        this.frame = frame;
        setBorder(Tema.padding(24, 28, 24, 28));

        Tombol btnBuat = new Tombol("Buat Turnamen", Tombol.Jenis.UTAMA);
        btnBuat.addActionListener(e -> buatTurnamen());
        btnHapusTurnamen.addActionListener(e -> hapusTurnamen());
        btnDaftarkan.addActionListener(e -> daftarkanTeam());
        btnHapusPeserta.addActionListener(e -> hapusPeserta());
        btnJadwal.addActionListener(e -> susunJadwal());
        btnBatalJadwal.addActionListener(e -> batalkanJadwal());
        daftar.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetail();
            }
        });
        tabelPeserta.getSelectionModel().addListSelectionListener(e -> aturTombol());

        add(Kerangka.kepala("Turnamen", "Mendaftarkan team ke turnamen, mengeluarkan peserta, dan menyusun jadwal pertandingan.",
                Kerangka.barisTombol(btnHapusTurnamen, btnBuat)), BorderLayout.NORTH);

        JScrollPane gulirDaftar = Tema.gulir(daftar);
        gulirDaftar.getViewport().setBackground(Tema.PERMUKAAN);
        gulirDaftar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Kartu kiri = Kerangka.kartu("Daftar turnamen", gulirDaftar);
        kiri.setPreferredSize(new Dimension(290, 0));

        wadahStatus.setOpaque(false);
        JPanel judul = new JPanel(new BorderLayout(0, 4));
        judul.setOpaque(false);
        judul.add(lblNama, BorderLayout.NORTH);
        judul.add(lblInfo, BorderLayout.CENTER);
        judul.add(wadahStatus, BorderLayout.SOUTH);

        JScrollPane gulirPeserta = Tema.gulir(tabelPeserta);
        gulirPeserta.getViewport().setBackground(Tema.PERMUKAAN);
        JPanel bawah = new JPanel(new BorderLayout(0, 8));
        bawah.setOpaque(false);
        bawah.add(new TeksBungkus("Setiap grup berisi 4 team, diundi acak saat jadwal disusun. Grup dimainkan Bo2, "
                + "babak gugur Bo3, dua teratas tiap grup lolos.", 12f, Tema.TEKS_REDUP, 640), BorderLayout.NORTH);
        bawah.add(Kerangka.barisTombol(btnBatalJadwal, btnHapusPeserta, btnDaftarkan, btnJadwal), BorderLayout.SOUTH);

        Kartu kanan = new Kartu(new BorderLayout(0, 12));
        kanan.setBorder(Tema.padding(16, 18, 16, 18));
        kanan.add(judul, BorderLayout.NORTH);
        kanan.add(gulirPeserta, BorderLayout.CENTER);
        kanan.add(bawah, BorderLayout.SOUTH);

        JPanel isi = new JPanel(new BorderLayout(16, 0));
        isi.setOpaque(false);
        isi.add(kiri, BorderLayout.WEST);
        isi.add(kanan, BorderLayout.CENTER);
        add(isi, BorderLayout.CENTER);

        if (!DataSementara.TURNAMEN.isEmpty()) {
            daftar.setSelectedIndex(0);
        } else {
            tampilkanDetail();
        }
    }

    private void tampilkanDetail() {
        Tournament t = daftar.getSelectedValue();
        tabelPeserta.kosongkan();
        wadahStatus.removeAll();
        if (t == null) {
            lblNama.setText("Pilih turnamen di daftar");
            lblInfo.setText(DataSementara.TURNAMEN.isEmpty() ? "Belum ada turnamen. Klik \"Buat Turnamen\" untuk membuatnya." : " ");
        } else {
            lblNama.setText(t.getNama());
            lblInfo.setText(t.getGame().getNama() + ", kuota " + t.getKuota() + " team, terdaftar " + t.getPeserta().size());
            int no = 1;
            for (Team tim : t.getPeserta()) {
                tabelPeserta.tambah(no++, tim.getNama(), tim.getRoster().size() + " pemain");
            }
            wadahStatus.add(new Lencana(t.statusTeks(), switch (t.statusTeks()) {
                case "Selesai" -> Lencana.Jenis.GUGUR;
                case "Berjalan" -> Lencana.Jenis.LOLOS;
                default -> Lencana.Jenis.GRUP;
            }));
            if (t.getJuara() != null) {
                wadahStatus.add(Tema.label("Juara: " + t.getJuara().getNama(), Font.BOLD, 13f));
            }
        }
        wadahStatus.revalidate();
        wadahStatus.repaint();
        aturTombol();
    }

    private void aturTombol() {
        Tournament t = daftar.getSelectedValue();
        boolean ada = t != null;
        boolean disusun = ada && t.isJadwalDisusun();
        btnHapusTurnamen.setEnabled(ada);
        btnDaftarkan.setEnabled(ada && !disusun && t.getPeserta().size() < t.getKuota());
        btnHapusPeserta.setEnabled(ada && !disusun && tabelPeserta.getSelectedRow() >= 0);
        btnJadwal.setEnabled(ada && !disusun && t.getPeserta().size() == t.getKuota());
        btnBatalJadwal.setEnabled(disusun && !t.adaHasil());
    }

    private void buatTurnamen() {
        Tournament baru = new TurnamenFormDialog(SwingUtilities.getWindowAncestor(this)).tampilkan();
        if (baru != null) {
            DataSementara.TURNAMEN.addElement(baru);
            daftar.setSelectedValue(baru, true);
            frame.catatAktivitas("Turnamen " + baru.getNama() + " dibuat (" + baru.getGame().getNama() + ", kuota " + baru.getKuota() + ")");
        }
    }

    private void hapusTurnamen() {
        Tournament t = daftar.getSelectedValue();
        if (t == null) {
            return;
        }
        String peringatan = t.adaHasil() ? " Semua skor dan statistik dari turnamen ini ikut terhapus." : "";
        if (Pesan.konfirmasi(this, "Hapus Turnamen", "Hapus turnamen " + t.getNama() + "?" + peringatan)) {
            t.bubarkan(); // tarik statistik dan buka kunci roster
            DataSementara.TURNAMEN.removeElement(t);
            frame.catatAktivitas("Turnamen " + t.getNama() + " dihapus");
            tampilkanDetail();
        }
    }

    private void daftarkanTeam() {
        Tournament t = daftar.getSelectedValue();
        if (t == null) {
            return;
        }
        List<Team> calon = new ArrayList<>();
        for (int i = 0; i < DataSementara.TEAM.size(); i++) {
            Team tim = DataSementara.TEAM.get(i);
            if (tim.getGame() == t.getGame() && !t.getPeserta().contains(tim)) {
                calon.add(tim);
            }
        }
        if (calon.isEmpty()) {
            Pesan.info(this, "Daftarkan Team", "Tidak ada team " + t.getGame().getNama()
                    + " yang bisa didaftarkan. Buat team baru di menu Team & Roster.");
            return;
        }
        List<Team> dipilih = new PilihDialog<>(SwingUtilities.getWindowAncestor(this), "Daftarkan Team ke " + t.getNama(),
                "Pilih team " + t.getGame().getNama() + ". Sistem memeriksa roster dan sisa kuota.", calon, Team::getNama,
                tim -> tim.getRoster().size() + " pemain, " + (tim.isLengkap() ? "roster lengkap" : "roster belum lengkap"),
                true).tampilkan();
        int diterima = 0;
        for (Team tim : dipilih) {
            try {
                t.terimaTeam(tim);
                diterima++;
            } catch (AturanTurnamenException ex) {
                Pesan.peringatan(this, "Daftarkan Team", ex.getMessage());
            }
        }
        if (diterima > 0) {
            frame.catatAktivitas(diterima + " team didaftarkan ke " + t.getNama() + " (" + t.getPeserta().size() + "/" + t.getKuota() + ")");
        }
        segarkan(t);
    }

    private void hapusPeserta() {
        Tournament t = daftar.getSelectedValue();
        int baris = tabelPeserta.getSelectedRow();
        if (t == null || baris < 0) {
            return;
        }
        Team tim = t.getPeserta().get(baris);
        if (!Pesan.konfirmasi(this, "Hapus Peserta", "Keluarkan " + tim.getNama() + " dari " + t.getNama() + "?")) {
            return;
        }
        try {
            t.hapusPeserta(tim);
            frame.catatAktivitas(tim.getNama() + " dikeluarkan dari " + t.getNama());
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Hapus Peserta", ex.getMessage());
        }
        segarkan(t);
    }

    private void susunJadwal() {
        Tournament t = daftar.getSelectedValue();
        if (t == null) {
            return;
        }
        if (!Pesan.konfirmasi(this, "Susun Jadwal", "Undi grup dan susun jadwal untuk " + t.getNama()
                + "? Setelah itu peserta dan roster team dikunci.")) {
            return;
        }
        try {
            t.buatMatch();
            frame.catatAktivitas("Jadwal " + t.getNama() + " disusun: " + t.getKlasemen().size() + " grup, "
                    + t.getPertandinganGrup().size() + " laga grup");
            Pesan.info(this, "Jadwal Disusun", "Grup sudah diundi. Lihat jadwal di menu Jadwal & Bracket, "
                    + "dan catat skor di menu Input Skor.");
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Susun Jadwal", ex.getMessage());
        }
        segarkan(t);
    }

    private void batalkanJadwal() {
        Tournament t = daftar.getSelectedValue();
        if (t == null || !Pesan.konfirmasi(this, "Batalkan Jadwal", "Batalkan jadwal " + t.getNama()
                + "? Undian grup dibuang dan pendaftaran dibuka lagi.")) {
            return;
        }
        try {
            t.batalkanJadwal();
            frame.catatAktivitas("Jadwal " + t.getNama() + " dibatalkan");
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Batalkan Jadwal", ex.getMessage());
        }
        segarkan(t);
    }

    private void segarkan(Tournament t) {
        daftar.repaint();
        tampilkanDetail();
        daftar.setSelectedValue(t, true);
    }
}
