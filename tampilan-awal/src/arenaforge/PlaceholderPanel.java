package arenaforge;

import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Halaman sementara untuk menu yang belum dibuat. */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String judul, String keterangan) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(Tema.padding(20, 20, 20, 20));

        JLabel lblInfo = Tema.label("Fitur ini akan dibuat pada tahap berikutnya.", Font.ITALIC, 12f);
        lblInfo.setForeground(Tema.TEKS_REDUP);

        add(Tema.label(judul, Font.BOLD, 20f));
        add(Box.createVerticalStrut(8));
        add(new JLabel(keterangan));
        add(Box.createVerticalStrut(16));
        add(lblInfo);
    }
}
