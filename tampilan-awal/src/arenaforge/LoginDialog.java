package arenaforge;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Layar pertama aplikasi (JDialog modal).
 * Panitia dan player masuk dengan username + password, penonton bisa langsung masuk.
 */
public class LoginDialog extends JDialog {
    // Akun sementara sebelum ada tabel user di database
    private static final String USERNAME_PANITIA = "admin";
    private static final String PASSWORD_PANITIA = "admin";
    private static final String USERNAME_PLAYER = "player";
    private static final String PASSWORD_PLAYER = "player";

    private final JRadioButton rbPanitia = new JRadioButton("Panitia", true);
    private final JRadioButton rbPlayer = new JRadioButton("Player");
    private final JRadioButton rbPenonton = new JRadioButton("Penonton");
    private final JTextField tfUsername = new JTextField(16);
    private final JPasswordField pfPassword = new JPasswordField(16);
    private final JCheckBox cbTampilkanPassword = new JCheckBox("Tampilkan password");
    private final char echoChar = pfPassword.getEchoChar();

    private String peran; // tetap null kalau dialog ditutup tanpa login

    public LoginDialog() {
        super((Frame) null, "ArenaForge - Masuk", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(Tema.header("ArenaForge", "Game Tournament Manager"), BorderLayout.NORTH);
        add(buatForm(), BorderLayout.CENTER);
        add(buatTombol(), BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
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
        pilihanPeran.add(rbPanitia);
        pilihanPeran.add(Box.createHorizontalStrut(12));
        pilihanPeran.add(rbPlayer);
        pilihanPeran.add(Box.createHorizontalStrut(12));
        pilihanPeran.add(rbPenonton);

        // GridLayout 4 baris x 2 kolom: label di kiri, input di kanan
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(Tema.padding(16, 16, 8, 16));
        form.add(new JLabel("Masuk sebagai"));
        form.add(pilihanPeran);
        form.add(new JLabel("Username"));
        form.add(tfUsername);
        form.add(new JLabel("Password"));
        form.add(pfPassword);
        form.add(new JLabel());
        form.add(cbTampilkanPassword);

        // Event: penonton tidak perlu mengisi username/password
        rbPanitia.addItemListener(e -> aturInputAkun());
        rbPlayer.addItemListener(e -> aturInputAkun());
        rbPenonton.addItemListener(e -> aturInputAkun());

        // Event: tampilkan atau sembunyikan isi password
        cbTampilkanPassword.addItemListener(e ->
                pfPassword.setEchoChar(cbTampilkanPassword.isSelected() ? (char) 0 : echoChar));
        return form;
    }

    private JPanel buatTombol() {
        JButton btnMasuk = new JButton("Masuk");
        JButton btnBatal = new JButton("Batal");
        btnMasuk.addActionListener(e -> prosesLogin());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnMasuk); // tekan Enter = klik Masuk

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.add(btnBatal);
        tombol.add(btnMasuk);

        JLabel petunjuk = Tema.label("Akun demo: panitia admin / admin, player player / player", Font.PLAIN, 11f);
        petunjuk.setForeground(Tema.TEKS_REDUP);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setBorder(Tema.padding(8, 16, 16, 16));
        bawah.add(petunjuk, BorderLayout.WEST);
        bawah.add(tombol, BorderLayout.EAST);
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
        boolean cocok = username.equals(USERNAME_PANITIA)
                && Arrays.equals(password, PASSWORD_PANITIA.toCharArray());
        Arrays.fill(password, '\0'); // hapus salinan password dari memori

        if (kosong) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.",
                    "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cocok) {
            JOptionPane.showMessageDialog(this, "Username atau password salah.",
                    "Login", JOptionPane.ERROR_MESSAGE);
            pfPassword.setText("");
            pfPassword.requestFocusInWindow();
            return;
        }
        peran = "Panitia";
        dispose();
    }
}
