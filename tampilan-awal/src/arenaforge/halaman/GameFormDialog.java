package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.FormDialog;
import arenaforge.ui.Formulir;
import arenaforge.ui.Isian;
import arenaforge.ui.Pesan;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Form tambah game baru: nama, jumlah pemain inti dan cadangan, serta daftar role. */
class GameFormDialog extends FormDialog {
    private final JTextField tfNama = Isian.teks(22);
    private final ComboGelap<Integer> cbInti = new ComboGelap<>(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    private final ComboGelap<Integer> cbCadangan = new ComboGelap<>(new Integer[] {0, 1, 2, 3, 4, 5});
    private final JTextField tfRole = Isian.teks(22);
    private Game hasil; // tetap null kalau dibatalkan

    GameFormDialog(Window induk) {
        super(induk, "Tambah Game");
        cbInti.setSelectedItem(5);
        cbCadangan.setSelectedItem(2);

        Formulir form = new Formulir()
                .tambah("Nama game", tfNama)
                .tambah("Pemain inti", cbInti)
                .tambah("Cadangan", cbCadangan)
                .tambah("Role", tfRole)
                .tambahLebar(new TeksBungkus("Tulis role dipisah koma, contoh: Duelist, Controller, Sentinel. "
                        + "Untuk game solo isi satu role saja, mis. Solo.", 12f, Tema.TEKS_REDUP, 380));
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

    Game tampilkan() {
        setVisible(true);
        return hasil;
    }

    private void simpan() {
        String nama = tfNama.getText().trim();
        List<String> role = new ArrayList<>();
        for (String bagian : tfRole.getText().split(",")) {
            String r = bagian.trim();
            if (!r.isEmpty() && role.stream().noneMatch(r::equalsIgnoreCase)) {
                role.add(r);
            }
        }
        if (DataSementara.cariGame(nama) != null) {
            Pesan.peringatan(this, "Tambah Game", "Game " + nama + " sudah ada.");
            return;
        }
        try {
            hasil = new Game(nama, (Integer) cbInti.getSelectedItem(), (Integer) cbCadangan.getSelectedItem(), role);
            dispose();
        } catch (IllegalArgumentException ex) {
            Pesan.peringatan(this, "Tambah Game", ex.getMessage());
        }
    }
}
