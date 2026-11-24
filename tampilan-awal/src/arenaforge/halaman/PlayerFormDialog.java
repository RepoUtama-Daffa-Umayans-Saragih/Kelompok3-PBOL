package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.model.Player;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.DaftarGelap;
import arenaforge.ui.FormDialog;
import arenaforge.ui.Formulir;
import arenaforge.ui.Isian;
import arenaforge.ui.Pesan;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Form registrasi player (JDialog modal).
 * Daftar role berubah otomatis mengikuti game yang dipilih di combo.
 */
class PlayerFormDialog extends FormDialog {
    private final JTextField tfId = Isian.teks(16);
    private final JTextField tfNickname = Isian.teks(16);
    private final ComboGelap<Game> cbGame = new ComboGelap<>(DataSementara.semuaGame());
    private final DaftarGelap<String> listRole = new DaftarGelap<>(r -> r, null);
    private final JLabel lblRoster = Tema.redup("");

    private Player hasil; // tetap null kalau dibatalkan

    PlayerFormDialog(Window induk) {
        super(induk, "Registrasi Player");
        listRole.setVisibleRowCount(5);
        JScrollPane gulirRole = Tema.gulir(listRole);
        gulirRole.getViewport().setBackground(Tema.PERMUKAAN);
        gulirRole.setPreferredSize(new Dimension(300, 150));

        Formulir form = new Formulir()
                .tambah("ID Player", tfId)
                .tambah("Nickname", tfNickname)
                .tambah("Game", cbGame)
                .tambah("Role", gulirRole)
                .tambahLebar(lblRoster);
        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.setBorder(Tema.padding(18, 20, 4, 20));
        isi.add(form, BorderLayout.CENTER);

        Tombol btnSimpan = new Tombol("Simpan", Tombol.Jenis.UTAMA);
        Tombol btnBatal = new Tombol("Batal", Tombol.Jenis.KEDUA);
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnSimpan);

        // Event: ganti game -> daftar role ikut diganti
        cbGame.addActionListener(e -> isiRole());
        isiRole();

        add(isi, BorderLayout.CENTER);
        add(barisTombol(btnBatal, btnSimpan), BorderLayout.SOUTH);
        selesaiDisusun();
    }

    /** Tampilkan form dan tunggu sampai ditutup. Hasilnya player baru, atau null kalau batal. */
    Player tampilkan() {
        setVisible(true);
        return hasil;
    }

    private void isiRole() {
        Game game = (Game) cbGame.getSelectedItem();
        listRole.modelIsi().clear();
        if (game == null) {
            return;
        }
        game.getRole().forEach(listRole.modelIsi()::addElement);
        if (game.getRole().size() == 1) {
            listRole.setSelectedIndex(0); // game solo hanya punya satu role
        }
        lblRoster.setText("Roster team " + game.getNama() + ": " + game.deskripsiRoster());
    }

    private void simpan() {
        String id = tfId.getText().trim();
        String nickname = tfNickname.getText().trim();
        String role = listRole.getSelectedValue();
        Game game = (Game) cbGame.getSelectedItem();

        String pesan = null;
        if (id.isEmpty() || nickname.isEmpty()) {
            pesan = "ID Player dan nickname wajib diisi.";
        } else if (DataSementara.idSudahDipakai(id)) {
            pesan = "ID " + id + " sudah dipakai player lain.";
        } else if (game == null) {
            pesan = "Belum ada game. Tambahkan dulu di menu Game & Role.";
        } else if (role == null) {
            pesan = "Pilih role untuk game " + game.getNama() + ".";
        }
        if (pesan != null) {
            Pesan.peringatan(this, "Registrasi Player", pesan);
            return;
        }
        try {
            hasil = new Player(id, nickname, game, role); // role divalidasi oleh Player.setRole()
            dispose();
        } catch (IllegalArgumentException ex) {
            Pesan.peringatan(this, "Registrasi Player", ex.getMessage());
        }
    }
}
