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
