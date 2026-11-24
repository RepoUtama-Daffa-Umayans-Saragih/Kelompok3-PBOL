package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.MainFrame;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.ui.Pesan;
import arenaforge.ui.TabelData;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/** Game & Role (khusus panitia): daftar game beserta ukuran roster dan role-nya, bisa ditambah dan dihapus. */
public class GamePanel extends JPanel {
    private final MainFrame frame;
    private final TabelData tabel = new TabelData("Game", "Roster", "Role").lebarKolom(0, 160).lebarKolom(1, 160).lebarKolom(2, 420);
    private final Tombol btnHapus = new Tombol("Hapus", Tombol.Jenis.KEDUA);

    public GamePanel(MainFrame frame) {
        super(new BorderLayout(0, 16));
        this.frame = frame;
        setBorder(Tema.padding(24, 28, 24, 28));

        Tombol btnTambah = new Tombol("Tambah Game", Tombol.Jenis.UTAMA);
        btnTambah.addActionListener(e -> tambahGame());
        btnHapus.setEnabled(false);
        btnHapus.addActionListener(e -> hapusGame());
        // Event: tombol Hapus hanya aktif kalau ada game yang dipilih
        tabel.getSelectionModel().addListSelectionListener(e -> btnHapus.setEnabled(tabel.getSelectedRow() >= 0));

        add(Kerangka.kepala("Game & Role", "Game yang bisa dipertandingkan, jumlah pemain tiap team, dan role yang dipilih saat registrasi player.",
                Kerangka.barisTombol(btnHapus, btnTambah)), BorderLayout.NORTH);
        JScrollPane gulir = Tema.gulir(tabel);
        gulir.getViewport().setBackground(Tema.PERMUKAAN);
        add(gulir, BorderLayout.CENTER);
        isiTabel();
    }

    private void isiTabel() {
        tabel.kosongkan();
        for (int i = 0; i < DataSementara.GAME.size(); i++) {
            Game g = DataSementara.GAME.get(i);
            tabel.tambah(g.getNama(), g.deskripsiRoster(), String.join(", ", g.getRole()));
        }
    }

    private void tambahGame() {
        Game baru = new GameFormDialog(SwingUtilities.getWindowAncestor(this)).tampilkan();
        if (baru != null) {
            DataSementara.GAME.addElement(baru);
            isiTabel();
            frame.catatAktivitas("Game " + baru.getNama() + " ditambahkan (" + baru.deskripsiRoster() + ", "
                    + baru.getRole().size() + " role)");
        }
    }

    private void hapusGame() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }
        Game game = DataSementara.GAME.get(baris);
        if (DataSementara.gameDipakai(game)) {
            Pesan.peringatan(this, "Hapus Game", "Game " + game.getNama()
                    + " masih dipakai player, team, atau turnamen, jadi tidak bisa dihapus.");
            return;
        }
        if (Pesan.konfirmasi(this, "Hapus Game", "Hapus game " + game.getNama() + "?")) {
            DataSementara.GAME.remove(baris);
            isiTabel();
            frame.catatAktivitas("Game " + game.getNama() + " dihapus");
        }
    }
}
