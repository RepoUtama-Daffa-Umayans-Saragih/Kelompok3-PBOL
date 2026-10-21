package arenaforge;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Form registrasi player (JDialog modal).
 * Daftar role (JList) berubah otomatis mengikuti game yang dipilih di JComboBox.
 */
public class PlayerFormDialog extends JDialog {
    private final JTextField tfId = new JTextField(14);
    private final JTextField tfNickname = new JTextField(14);
    private final JComboBox<String> cbGame = new JComboBox<>(DataSementara.GAME);
    private final DefaultListModel<String> modelRole = new DefaultListModel<>();
    private final JList<String> listRole = new JList<>(modelRole);
    private final JLabel lblRoster = new JLabel();

    private Player hasil; // tetap null kalau dibatalkan

    public PlayerFormDialog(Frame owner) {
        super(owner, "Registrasi Player", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buatForm(), BorderLayout.CENTER);
        add(buatTombol(), BorderLayout.SOUTH);

        // Event: ganti game -> daftar role ikut diganti
        cbGame.addActionListener(e -> isiRole());
        isiRole();

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /** Tampilkan form dan tunggu sampai ditutup. Hasilnya player baru, atau null kalau batal. */
    public Player tampilkan() {
        setVisible(true);
        return hasil;
    }

    private JPanel buatForm() {
        // GridLayout 3 x 2: label di kiri, input di kanan
        JPanel isian = new JPanel(new GridLayout(3, 2, 8, 8));
        isian.add(new JLabel("ID Player"));
        isian.add(tfId);
        isian.add(new JLabel("Nickname"));
        isian.add(tfNickname);
        isian.add(new JLabel("Game"));
        isian.add(cbGame);

        listRole.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRole.setVisibleRowCount(5);
        JScrollPane scrollRole = new JScrollPane(listRole);
        scrollRole.setBorder(BorderFactory.createTitledBorder("Role"));

        lblRoster.setForeground(Tema.TEKS_REDUP);

        isian.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRoster.setAlignmentX(Component.LEFT_ALIGNMENT);

        // BoxLayout vertikal: isian, daftar role, lalu info roster
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(Tema.padding(16, 16, 8, 16));
        form.add(isian);
        form.add(Box.createVerticalStrut(12));
        form.add(scrollRole);
        form.add(Box.createVerticalStrut(8));
        form.add(lblRoster);
        return form;
    }

    private JPanel buatTombol() {
        JButton btnSimpan = new JButton("Simpan");
        JButton btnBatal = new JButton("Batal");
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnSimpan);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setBorder(Tema.padding(8, 16, 16, 16));
        tombol.add(btnBatal);
        tombol.add(btnSimpan);
        return tombol;
    }

    private void isiRole() {
        String game = (String) cbGame.getSelectedItem();
        modelRole.clear();
        for (String role : DataSementara.roleUntuk(game)) {
            modelRole.addElement(role);
        }
        if (modelRole.size() == 1) {
            listRole.setSelectedIndex(0); // game solo hanya punya satu role
        }
        lblRoster.setText("Roster team " + game + ": " + DataSementara.rosterUntuk(game));
    }

    private void simpan() {
        String id = tfId.getText().trim();
        String nickname = tfNickname.getText().trim();
        String role = listRole.getSelectedValue();

        String pesan = null;
        if (id.isEmpty() || nickname.isEmpty()) {
            pesan = "ID Player dan nickname wajib diisi.";
        } else if (DataSementara.idSudahDipakai(id)) {
            pesan = "ID " + id + " sudah dipakai player lain.";
        } else if (role == null) {
            pesan = "Pilih role untuk game " + cbGame.getSelectedItem() + ".";
        }
        if (pesan != null) {
            JOptionPane.showMessageDialog(this, pesan, "Registrasi Player", JOptionPane.WARNING_MESSAGE);
            return;
        }

        hasil = new Player(id, nickname, (String) cbGame.getSelectedItem(), role);
        dispose();
    }
}
