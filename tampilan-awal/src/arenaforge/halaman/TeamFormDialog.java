package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.model.Team;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.FormDialog;
import arenaforge.ui.Formulir;
import arenaforge.ui.Isian;
import arenaforge.ui.Pesan;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Form membuat team baru: nama dan game. ID team dibuat otomatis. */
class TeamFormDialog extends FormDialog {
    private final JTextField tfNama = Isian.teks(20);
    private final ComboGelap<Game> cbGame = new ComboGelap<>(DataSementara.semuaGame());
    private Team hasil; // tetap null kalau dibatalkan

    TeamFormDialog(Window induk) {
        super(induk, "Buat Team");
        Formulir form = new Formulir().tambah("Nama team", tfNama).tambah("Game", cbGame);
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

    Team tampilkan() {
        setVisible(true);
        return hasil;
    }

    private void simpan() {
        String nama = tfNama.getText().trim();
        if (nama.isEmpty()) {
            Pesan.peringatan(this, "Buat Team", "Nama team wajib diisi.");
            return;
        }
        if (DataSementara.namaTeamDipakai(nama)) {
            Pesan.peringatan(this, "Buat Team", "Nama team " + nama + " sudah dipakai team lain.");
            return;
        }
        Game game = (Game) cbGame.getSelectedItem();
        if (game == null) {
            Pesan.peringatan(this, "Buat Team", "Belum ada game. Tambahkan dulu di menu Game & Role.");
            return;
        }
        hasil = new Team(DataSementara.idTeamBerikut(), nama, game);
        dispose();
    }
}
