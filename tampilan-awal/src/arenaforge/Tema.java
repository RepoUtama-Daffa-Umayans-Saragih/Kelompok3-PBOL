package arenaforge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/** Warna dan helper tampilan yang dipakai di semua layar. */
public final class Tema {
    public static final Color HEADER = new Color(0x1E2A44);
    public static final Color AKSEN = new Color(0xF2A541);
    public static final Color SIDEBAR = new Color(0xEEF1F6);
    public static final Color GARIS = new Color(0xD5DAE3);
    public static final Color TEKS_REDUP = new Color(0x6B7280);

    private Tema() {
    }

    /** Membuat JLabel dengan gaya (Font.BOLD / Font.PLAIN) dan ukuran huruf tertentu. */
    public static JLabel label(String teks, int gaya, float ukuran) {
        JLabel label = new JLabel(teks);
        label.setFont(label.getFont().deriveFont(gaya, ukuran));
        return label;
    }

    public static Border padding(int atas, int kiri, int bawah, int kanan) {
        return BorderFactory.createEmptyBorder(atas, kiri, bawah, kanan);
    }

    /**
     * Panel header gelap berisi judul dan subjudul di sisi kiri.
     * Memakai BorderLayout, jadi sisi EAST bisa diisi oleh pemanggil.
     */
    public static JPanel header(String judul, String subjudul) {
        JLabel lblJudul = label(judul, Font.BOLD, 22f);
        lblJudul.setForeground(Color.WHITE);
        JLabel lblSubjudul = label(subjudul, Font.PLAIN, 12f);
        lblSubjudul.setForeground(AKSEN);

        JPanel teks = new JPanel(new GridLayout(2, 1));
        teks.setOpaque(false);
        teks.add(lblJudul);
        teks.add(lblSubjudul);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER);
        header.setBorder(padding(12, 16, 12, 16));
        header.add(teks, BorderLayout.WEST);
        return header;
    }
}
