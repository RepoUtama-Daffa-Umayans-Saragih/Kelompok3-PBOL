package arenaforge;

import arenaforge.ui.FormDialog;
import arenaforge.ui.Formulir;
import arenaforge.ui.IkonPilihan;
import arenaforge.ui.Isian;
import arenaforge.ui.Pesan;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Layar pertama aplikasi (JDialog modal).
 * Panitia dan player masuk dengan username + password, penonton bisa langsung masuk.
 */
public class LoginDialog extends FormDialog {
    // Akun sementara sebelum ada tabel user di database
    private static final String USERNAME_PANITIA = "admin";
    private static final String PASSWORD_PANITIA = "admin";
    private static final String USERNAME_PLAYER = "player";
    private static final String PASSWORD_PLAYER = "player";

    private final JRadioButton rbPanitia = IkonPilihan.gayai(new JRadioButton("Panitia", true), true);
    private final JRadioButton rbPlayer = IkonPilihan.gayai(new JRadioButton("Player"), true);
    private final JRadioButton rbPenonton = IkonPilihan.gayai(new JRadioButton("Penonton"), true);
    private final JTextField tfUsername = Isian.teks(16);
    private final JPasswordField pfPassword = Isian.sandi(16);
    private final JCheckBox cbTampilkanPassword = IkonPilihan.gayai(new JCheckBox("Tampilkan password"), false);
    private final char echoChar = pfPassword.getEchoChar();

    private String peran; // tetap null kalau dialog ditutup tanpa login

    public LoginDialog() {
        super((Frame) null, "ArenaForge - Masuk");
        add(Tema.header("ArenaForge", "Game Tournament Manager"), BorderLayout.NORTH);
        add(buatForm(), BorderLayout.CENTER);
        add(buatTombol(), BorderLayout.SOUTH);
        selesaiDisusun();
    }

    /** Tampilkan dialog dan tunggu sampai ditutup. Hasilnya "Panitia", "Player", "Penonton", atau null. */
    public String tampilkan() {
        setVisible(true);
        return peran;
    }

    private JPanel buatForm() {
        ButtonGroup grupPeran = new ButtonGroup();
        grupPeran.add(rbPanitia);
        grupPeran.add(rbPlayer);
        grupPeran.add(rbPenonton);

        JPanel pilihanPeran = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pilihanPeran.setOpaque(false);
        pilihanPeran.add(rbPanitia);
        pilihanPeran.add(Box.createHorizontalStrut(14));
        pilihanPeran.add(rbPlayer);
        pilihanPeran.add(Box.createHorizontalStrut(14));
        pilihanPeran.add(rbPenonton);

        // Formulir dua kolom: label di kiri, input di kanan
        Formulir form = new Formulir()
                .tambah("Masuk sebagai", pilihanPeran)
                .tambah("Username", tfUsername)
                .tambah("Password", pfPassword)
                .tambah("", cbTampilkanPassword);
        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.setBorder(Tema.padding(18, 20, 6, 20));
        isi.add(form, BorderLayout.CENTER);

        // Event: penonton tidak perlu mengisi username/password
        rbPanitia.addItemListener(e -> aturInputAkun());
        rbPlayer.addItemListener(e -> aturInputAkun());
        rbPenonton.addItemListener(e -> aturInputAkun());

        // Event: tampilkan atau sembunyikan isi password
        cbTampilkanPassword.addItemListener(e ->
                pfPassword.setEchoChar(cbTampilkanPassword.isSelected() ? (char) 0 : echoChar));
        return isi;
    }

    private JPanel buatTombol() {
        Tombol btnMasuk = new Tombol("Masuk", Tombol.Jenis.UTAMA);
        Tombol btnBatal = new Tombol("Batal", Tombol.Jenis.KEDUA);
        btnMasuk.addActionListener(e -> prosesLogin());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnMasuk); // tekan Enter = klik Masuk

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(btnBatal);
        tombol.add(btnMasuk);

        JPanel bawah = new JPanel(new BorderLayout(0, 10));
        bawah.setOpaque(false);
        bawah.setBorder(Tema.padding(8, 20, 18, 20));
        bawah.add(new TeksBungkus("Akun demo: panitia admin / admin, player player / player. Penonton langsung masuk.",
                12f, Tema.TEKS_REDUP, 360), BorderLayout.NORTH);
        bawah.add(tombol, BorderLayout.SOUTH);
        return bawah;
    }

    private void aturInputAkun() {
        boolean perluAkun = !rbPenonton.isSelected();
        tfUsername.setEnabled(perluAkun);
        pfPassword.setEnabled(perluAkun);
        cbTampilkanPassword.setEnabled(perluAkun);
    }

    private void prosesLogin() {
        if (rbPenonton.isSelected()) {
            peran = "Penonton";
            dispose();
            return;
        }

        boolean player = rbPlayer.isSelected();
        String usernameBenar = player ? USERNAME_PLAYER : USERNAME_PANITIA;
        String passwordBenar = player ? PASSWORD_PLAYER : PASSWORD_PANITIA;

        String username = tfUsername.getText().trim();
        char[] password = pfPassword.getPassword();
        boolean kosong = username.isEmpty() || password.length == 0;
        boolean cocok = username.equals(usernameBenar)
                && Arrays.equals(password, passwordBenar.toCharArray());
        Arrays.fill(password, '\0'); // hapus salinan password dari memori

        if (kosong) {
            Pesan.peringatan(this, "Login", "Username dan password wajib diisi.");
            return;
        }
        if (!cocok) {
            Pesan.galat(this, "Login", "Username atau password salah.");
            pfPassword.setText("");
            pfPassword.requestFocusInWindow();
            return;
        }
        peran = player ? "Player" : "Panitia";
        dispose();
    }
}
