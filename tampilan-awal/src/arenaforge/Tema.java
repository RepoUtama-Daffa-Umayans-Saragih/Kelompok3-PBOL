package arenaforge;

import arenaforge.ui.GulirGelap;
import arenaforge.ui.TeksBungkus;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Warna, huruf, dan helper tampilan yang dipakai di semua layar.
 * Semuanya Swing/AWT murni: tidak ada CSS, HTML, atau JavaFX.
 */
public final class Tema {
    // Palet ArenaForge: hitam, marun, merah tua, merah
    public static final Color HITAM = new Color(0x000000);
    public static final Color MARUN = new Color(0x840000);
    public static final Color MERAH_TUA = new Color(0xBD0000);
    public static final Color MERAH = new Color(0xFF0000);

    // Turunan palet untuk permukaan di atas latar hitam
    public static final Color PERMUKAAN = new Color(0x120000);
    public static final Color PERMUKAAN_TERANG = new Color(0x240000);
    public static final Color GARIS = new Color(0x3A0000);
    public static final Color PADAM = new Color(0x2A0000);
    public static final Color ABU = new Color(0x333333);
    public static final Color TEKS = Color.WHITE;
    public static final Color TEKS_REDUP = new Color(0xB8B8B8);
    public static final Color TEKS_TBD = new Color(0x8C8C8C);

    private static final String KELUARGA = pilihKeluarga();

    private Tema() {
    }

    /** Segoe UI kalau ada (Windows), kalau tidak pakai huruf sans-serif bawaan Java. */
    private static String pilihKeluarga() {
        Set<String> ada = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String nama : new String[] {"Segoe UI", "Helvetica Neue", "Noto Sans", "DejaVu Sans"}) {
            if (ada.contains(nama)) {
                return nama;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font font(int gaya, float ukuran) {
        return new Font(KELUARGA, gaya, 1).deriveFont(gaya, ukuran);
    }

    /** Huruf bermonospace untuk kode lomba, supaya huruf dan angka mudah dibedakan. */
    public static Font fontKode(float ukuran) {
        return new Font(Font.MONOSPACED, Font.BOLD, 1).deriveFont(Font.BOLD, ukuran);
    }

    /** Pasang tampilan gelap untuk semua komponen bawaan Swing. Panggil sekali, sebelum jendela dibuat. */
    public static void terapkan() {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception e) {
            // Kalau gagal, tampilan bawaan Java tetap bisa dipakai
        }
        UIManager.put("Panel.background", HITAM);
        UIManager.put("Label.foreground", TEKS);
        UIManager.put("Label.font", font(Font.PLAIN, 13f));
        UIManager.put("Viewport.background", HITAM);
        UIManager.put("ScrollPane.background", HITAM);
        UIManager.put("List.background", PERMUKAAN);
        UIManager.put("List.foreground", TEKS);
        UIManager.put("List.selectionBackground", MARUN);
        UIManager.put("List.selectionForeground", TEKS);
        UIManager.put("List.font", font(Font.PLAIN, 13f));
        UIManager.put("TextField.background", PERMUKAAN);
        UIManager.put("TextField.foreground", TEKS);
        UIManager.put("TextField.caretForeground", TEKS);
        UIManager.put("TextField.selectionBackground", MARUN);
        UIManager.put("TextField.selectionForeground", TEKS);
        UIManager.put("TextField.font", font(Font.PLAIN, 14f));
        UIManager.put("PasswordField.background", PERMUKAAN);
        UIManager.put("PasswordField.foreground", TEKS);
        UIManager.put("PasswordField.caretForeground", TEKS);
        UIManager.put("PasswordField.font", font(Font.PLAIN, 14f));
        UIManager.put("TextArea.background", PERMUKAAN);
        UIManager.put("TextArea.foreground", TEKS);
        UIManager.put("TextArea.caretForeground", TEKS);
        UIManager.put("TextArea.font", font(Font.PLAIN, 13f));
        UIManager.put("ComboBox.background", PERMUKAAN);
        UIManager.put("ComboBox.foreground", TEKS);
        UIManager.put("ComboBox.disabledBackground", PERMUKAAN);
        UIManager.put("ComboBox.disabledForeground", TEKS_TBD);
        UIManager.put("ComboBox.selectionBackground", MARUN);
        UIManager.put("ComboBox.selectionForeground", TEKS);
        UIManager.put("ToolTip.background", PERMUKAAN_TERANG);
        UIManager.put("ToolTip.foreground", TEKS);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(MARUN));
        UIManager.put("TitledBorder.titleColor", TEKS_REDUP);
    }

    // ---- Label dan teks ----

    /** Membuat JLabel putih dengan gaya (Font.BOLD / Font.PLAIN) dan ukuran huruf tertentu. */
    public static JLabel label(String teks, int gaya, float ukuran) {
        JLabel label = new JLabel(teks);
        label.setFont(font(gaya, ukuran));
        label.setForeground(TEKS);
        return label;
    }

    /** Label abu-abu untuk keterangan. */
    public static JLabel redup(String teks) {
        JLabel label = label(teks, Font.PLAIN, 13f);
        label.setForeground(TEKS_REDUP);
        return label;
    }

    public static JLabel redup(String teks, int gaya, float ukuran) {
        JLabel label = label(teks, gaya, ukuran);
        label.setForeground(TEKS_REDUP);
        return label;
    }

    public static Border padding(int atas, int kiri, int bawah, int kanan) {
        return BorderFactory.createEmptyBorder(atas, kiri, bawah, kanan);
    }

    /** Judul halaman besar dengan satu kalimat penjelasan di bawahnya. */
    public static JPanel kepalaHalaman(String judul, String penjelasan) {
        JPanel kepala = new JPanel();
        kepala.setLayout(new BoxLayout(kepala, BoxLayout.Y_AXIS));
        kepala.setOpaque(false);
        JLabel lblJudul = label(judul, Font.BOLD, 26f);
        lblJudul.setAlignmentX(0f);
        kepala.add(lblJudul);
        if (penjelasan != null && !penjelasan.isEmpty()) {
            TeksBungkus teks = new TeksBungkus(penjelasan, 13f, TEKS_REDUP, 640);
            teks.setAlignmentX(0f);
            teks.setBorder(padding(6, 0, 0, 0));
            kepala.add(teks);
        }
        return kepala;
    }

    /**
     * Panel header hitam berisi judul putih dan subjudul merah di sisi kiri.
     * Memakai BorderLayout, jadi sisi EAST bisa diisi oleh pemanggil.
     */
    public static JPanel header(String judul, String subjudul) {
        JPanel teks = new JPanel(new java.awt.GridLayout(2, 1));
        teks.setOpaque(false);
        teks.add(label(judul, Font.BOLD, 22f));
        JLabel sub = label(subjudul, Font.PLAIN, 12f);
        sub.setForeground(MERAH);
        teks.add(sub);

        JPanel header = new JPanel(new java.awt.BorderLayout());
        header.setBackground(HITAM);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, MARUN), padding(12, 16, 12, 16)));
        header.add(teks, java.awt.BorderLayout.WEST);
        return header;
    }

    /** JScrollPane gelap tanpa bingkai, dengan scrollbar marun. */
    public static JScrollPane gulir(java.awt.Component isi) {
        return GulirGelap.bungkus(isi);
    }
}
