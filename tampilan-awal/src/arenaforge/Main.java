package arenaforge;

import arenaforge.user.UserFrame;
import javax.swing.SwingUtilities;

/** Titik masuk aplikasi ArenaForge. Seluruh antarmuka memakai Java Swing/AWT. */
public class Main {

    public static void main(String[] args) {
        // Semua komponen Swing dibuat di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Tema.terapkan(); // tampilan hitam-merah untuk semua komponen
            mulai();
        });
    }

    /** Tampilkan dialog login, lalu buka jendela utama sesuai peran yang dipilih. */
    public static void mulai() {
        String peran = new LoginDialog().tampilkan();
        if (peran == null) {
            System.exit(0);
        }
        if ("Player".equals(peran)) {
            new UserFrame().setVisible(true);
        } else {
            new MainFrame(peran).setVisible(true);
        }
    }
}
