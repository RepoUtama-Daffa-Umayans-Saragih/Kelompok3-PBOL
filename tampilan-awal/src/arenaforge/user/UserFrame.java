package arenaforge.user;

import arenaforge.Main;
import arenaforge.Tema;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Jendela untuk peran Player. Bingkai, header, dan dialog memakai Swing,
 * sedangkan isi halamannya (PortalPlayer) dibuat dengan JavaFX dan ditempel lewat JFXPanel.
 */
public class UserFrame extends JFrame {
    private final CardLayout tumpukan = new CardLayout();
    private final JPanel isi = new JPanel(tumpukan);
    private final JLabel lblPesan = new JLabel("Memuat portal player...", SwingConstants.CENTER);

    public UserFrame() {
        super("ArenaForge - Portal Player");

        // Event: minta konfirmasi sebelum jendela ditutup
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                konfirmasiTutup();
            }
        });

        // Membuat JFXPanel sekaligus menyalakan JavaFX
        JFXPanel panelFx = new JFXPanel();
        // JavaFX jangan ikut berhenti saat jendela ini ditutup (keluar akun),
        // karena player yang masuk lagi memakai JavaFX yang sama
        Platform.setImplicitExit(false);

        lblPesan.setForeground(Color.WHITE);
        JPanel panelPesan = new JPanel(new BorderLayout());
        panelPesan.setBackground(Tema.HITAM);
        panelPesan.setBorder(Tema.padding(24, 24, 24, 24));
        panelPesan.add(lblPesan, BorderLayout.CENTER);

        // CardLayout: pesan "memuat" tampil dulu, lalu diganti JavaFX setelah Scene siap
        isi.add(panelPesan, "pesan");
        isi.add(panelFx, "portal");
        tumpukan.show(isi, "pesan");

        add(buatHeader(), BorderLayout.NORTH);
        add(isi, BorderLayout.CENTER);

        // Scene JavaFX wajib dibuat di JavaFX Application Thread, bukan di thread Swing
        Platform.runLater(() -> {
            try {
                panelFx.setScene(new PortalPlayer(DataPortal.NAMA_PLAYER, DataPortal.turnamenDiikuti()).buatScene());
                SwingUtilities.invokeLater(() -> tumpukan.show(isi, "portal"));
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> tampilkanGagal(ex));
            }
        });

        setSize(1120, 760);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);
    }

    private JPanel buatHeader() {
        JLabel lblPlayer = new JLabel("Masuk sebagai: " + DataPortal.NAMA_PLAYER + " (" + DataPortal.ID_PLAYER + ")");
        lblPlayer.setForeground(Color.WHITE);
        JButton btnKeluarAkun = new JButton("Keluar Akun");
        btnKeluarAkun.addActionListener(e -> keluarAkun());

        JPanel kanan = new JPanel();
        kanan.setLayout(new BoxLayout(kanan, BoxLayout.X_AXIS));
        kanan.setOpaque(false);
        kanan.add(lblPlayer);
        kanan.add(Box.createHorizontalStrut(12));
        kanan.add(btnKeluarAkun);

        JPanel header = Tema.header("ArenaForge", "Portal Player");
        // Garis marun memisahkan header Swing dari isi JavaFX yang sama-sama hitam
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Tema.MARUN),
                Tema.padding(12, 16, 12, 16)));
        header.add(kanan, BorderLayout.EAST);
        return header;
    }

    private void tampilkanGagal(Exception ex) {
        String sebab = String.valueOf(ex).replace("&", "&amp;").replace("<", "&lt;");
        lblPesan.setText("<html><div style='text-align:center'>Portal player gagal dimuat.<br>" + sebab
                + "<br><br>Keluar akun lalu masuk lagi. Kalau masih gagal, kirim pesan ini ke panitia.</div></html>");
    }

    private void keluarAkun() {
        int pilihan = JOptionPane.showConfirmDialog(this, "Keluar dari akun player?",
                "Keluar Akun", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            dispose();
