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
