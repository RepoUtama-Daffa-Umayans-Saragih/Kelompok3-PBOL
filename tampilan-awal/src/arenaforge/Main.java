package arenaforge;

import arenaforge.user.UserFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Titik masuk aplikasi ArenaForge (versi Swing, tanpa framework). */
public class Main {

    public static void main(String[] args) {
        // Semua komponen Swing dibuat di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Kalau tampilan sistem tidak tersedia, pakai tampilan bawaan Java
            }
            mulai();
        });
    }

    /** Tampilkan dialog login, lalu buka jendela utama sesuai peran yang dipilih. */
    static void mulai() {
        String peran = new LoginDialog().tampilkan();
        if (peran == null) {
            System.exit(0);
        }
        new MainFrame(peran).setVisible(true);
    }
}
