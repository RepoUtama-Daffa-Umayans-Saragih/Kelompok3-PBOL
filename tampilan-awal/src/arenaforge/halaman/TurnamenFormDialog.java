package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.model.Tournament;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.FormDialog;
import arenaforge.ui.Formulir;
import arenaforge.ui.Isian;
import arenaforge.ui.Pesan;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Form membuat turnamen baru: nama, game, dan kuota team. */
class TurnamenFormDialog extends FormDialog {
    private final JTextField tfNama = Isian.teks(22);
    private final ComboGelap<Game> cbGame = new ComboGelap<>(DataSementara.semuaGame());
    private final ComboGelap<Integer> cbKuota = new ComboGelap<>(new Integer[] {4, 8, 16});
    private Tournament hasil; // tetap null kalau dibatalkan

    TurnamenFormDialog(Window induk) {
        super(induk, "Buat Turnamen");
        cbKuota.setSelectedItem(8);
        Formulir form = new Formulir()
                .tambah("Nama turnamen", tfNama)
                .tambah("Game", cbGame)
                .tambah("Kuota team", cbKuota)
                .tambahLebar(new TeksBungkus("Setiap grup berisi 4 team. Kuota 4 = 1 grup, 8 = 2 grup, 16 = 4 grup. "
                        + "Dua teratas tiap grup lolos ke babak gugur.", 12f, Tema.TEKS_REDUP, 380));
        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.setBorder(Tema.padding(18, 20, 4, 20));
        isi.add(form, BorderLayout.CENTER);

        Tombol btnSimpan = new Tombol("Simpan", Tombol.Jenis.UTAMA);
        Tombol btnBatal = new Tombol("Batal", Tombol.Jenis.KEDUA);
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnSimpan);

        add(isi, BorderLayout.CENTER);
        add(barisTombol(btnBatal, btnSimpan), BorderLayout.SOUTH);
        selesaiDisusun();
    }

    Tournament tampilkan() {
        setVisible(true);
        return hasil;
    }

    private void simpan() {
        String nama = tfNama.getText().trim();
        if (nama.isEmpty()) {
            Pesan.peringatan(this, "Buat Turnamen", "Nama turnamen wajib diisi.");
            return;
        }
        if (DataSementara.namaTurnamenDipakai(nama)) {
            Pesan.peringatan(this, "Buat Turnamen", "Nama turnamen " + nama + " sudah dipakai.");
            return;
        }
        Game game = (Game) cbGame.getSelectedItem();
        if (game == null) {
            Pesan.peringatan(this, "Buat Turnamen", "Belum ada game. Tambahkan dulu di menu Game & Role.");
            return;
        }
        hasil = new Tournament(nama, game, (Integer) cbKuota.getSelectedItem());
        dispose();
    }
}
