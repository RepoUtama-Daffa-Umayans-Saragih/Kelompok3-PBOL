package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.MainFrame;
import arenaforge.Tema;
import arenaforge.model.AturanTurnamenException;
import arenaforge.model.Player;
import arenaforge.ui.Pesan;
import arenaforge.ui.TabelData;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Halaman Player (khusus panitia): daftar player dengan tombol tambah dan hapus.
 * Tombol tambah membuka PlayerFormDialog.
 */
public class PlayerPanel extends JPanel {
    private final MainFrame frame;
    private final TabelData tabel = new TabelData("ID", "Nickname", "Game", "Role", "Team")
            .lebarKolom(0, 70).lebarKolom(1, 150).lebarKolom(2, 140).lebarKolom(3, 130).lebarKolom(4, 160);
    private final Tombol btnHapus = new Tombol("Hapus", Tombol.Jenis.KEDUA);
    private final JLabel lblJumlah = Tema.redup("");

    public PlayerPanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setBorder(Tema.padding(24, 28, 24, 28));

        Tombol btnTambah = new Tombol("Tambah Player", Tombol.Jenis.UTAMA);
        btnTambah.addActionListener(e -> tambahPlayer());
        btnHapus.setEnabled(false);
        btnHapus.addActionListener(e -> hapusPlayer());
        // Event: tombol Hapus hanya aktif kalau ada player yang dipilih
        tabel.getSelectionModel().addListSelectionListener(e -> btnHapus.setEnabled(tabel.getSelectedRow() >= 0));

        add(Kerangka.kepala("Player", "Registrasi player beserta game dan role-nya.", Kerangka.barisTombol(btnHapus, btnTambah)),
                BorderLayout.NORTH);
        JScrollPane gulir = Tema.gulir(tabel);
        gulir.getViewport().setBackground(Tema.PERMUKAAN);
        add(gulir, BorderLayout.CENTER);
        lblJumlah.setFont(Tema.font(Font.PLAIN, 13f));
        add(lblJumlah, BorderLayout.SOUTH);
        isiTabel();
    }

    private void isiTabel() {
        tabel.kosongkan();
        for (int i = 0; i < DataSementara.PLAYER.size(); i++) {
            Player p = DataSementara.PLAYER.get(i);
            tabel.tambah(p.getId(), p.getNickname(), p.getGame().getNama(), p.getRole(),
                    p.getTeam() == null ? "-" : p.getTeam().getNama());
        }
        int jumlah = DataSementara.PLAYER.getSize();
        lblJumlah.setText(jumlah == 0
                ? "Belum ada player. Klik \"Tambah Player\" untuk mendaftarkan."
                : "Jumlah player: " + jumlah);
    }

    private void tambahPlayer() {
        Player player = new PlayerFormDialog(SwingUtilities.getWindowAncestor(this)).tampilkan();
        if (player != null) {
            DataSementara.PLAYER.addElement(player);
            isiTabel();
            frame.catatAktivitas("Player " + player.getNickname() + " (" + player.getId()
                    + ") terdaftar di " + player.getGame().getNama() + " sebagai " + player.getRole());
        }
    }

    private void hapusPlayer() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }
        Player player = DataSementara.PLAYER.get(baris);
        if (player.getTeam() != null && player.getTeam().isTerkunci()) {
            Pesan.peringatan(this, "Hapus Player", player.getNickname() + " ada di roster " + player.getTeam().getNama()
                    + " yang sedang dikunci karena ikut turnamen berjalan.");
            return;
        }
        if (!Pesan.konfirmasi(this, "Hapus Player", "Hapus player " + player.getNickname() + "?")) {
            return;
        }
        try {
            if (player.getTeam() != null) {
                player.getTeam().hapusPlayer(player); // keluarkan dari roster dulu
            }
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Hapus Player", ex.getMessage());
            return;
        }
        DataSementara.PLAYER.remove(baris);
        isiTabel();
        frame.catatAktivitas("Player " + player.getNickname() + " (" + player.getId() + ") dihapus");
    }
}
