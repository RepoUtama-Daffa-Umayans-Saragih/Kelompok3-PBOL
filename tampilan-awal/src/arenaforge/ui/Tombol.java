package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Tombol yang digambar sendiri (paintComponent) supaya sesuai palet ArenaForge.
 * UTAMA = isi merah tua, KEDUA = hanya bingkai, MENU = item menu samping yang bisa "aktif".
 */
public class Tombol extends JButton {
    public enum Jenis { UTAMA, KEDUA, MENU }

    private static boolean lewatKeyboard;

    static {
        // Cincin fokus hanya muncul kalau fokus dipindah lewat keyboard, bukan saat jendela baru dibuka atau diklik mouse
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e instanceof java.awt.event.KeyEvent ke && ke.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
                lewatKeyboard = true;
            } else if (e instanceof java.awt.event.MouseEvent me && me.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED) {
                lewatKeyboard = false;
            }
        }, java.awt.AWTEvent.KEY_EVENT_MASK | java.awt.AWTEvent.MOUSE_EVENT_MASK);
    }

    private final Jenis jenis;
    private boolean aktif;

    /** True kalau masukan terakhir pengguna berasal dari keyboard. */
    public static boolean fokusKeyboard() {
        return lewatKeyboard;
    }

    public Tombol(String teks, Jenis jenis) {
        super(teks);
        this.jenis = jenis;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setMargin(new Insets(0, 0, 0, 0));
        setFont(Tema.font(jenis == Jenis.UTAMA ? java.awt.Font.BOLD : java.awt.Font.PLAIN, jenis == Jenis.MENU ? 14f : 13f));
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        if (jenis == Jenis.MENU) {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
    }

    public Tombol(String teks) {
        this(teks, Jenis.UTAMA);
    }

    /** Hanya berarti untuk jenis MENU: tandai menu yang sedang dibuka. */
    public void setAktif(boolean aktif) {
        this.aktif = aktif;
        repaint();
    }

    public boolean isAktif() {
        return aktif;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int lebarTeks = fm.stringWidth(getText());
        return jenis == Jenis.MENU
                ? new Dimension(lebarTeks + 28, fm.getHeight() + 18)
                : new Dimension(lebarTeks + 36, fm.getHeight() + 14);
    }

    @Override
    public Dimension getMaximumSize() {
        return jenis == Jenis.MENU ? new Dimension(Integer.MAX_VALUE, getPreferredSize().height) : super.getMaximumSize();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ButtonModel m = getModel();
        boolean nyala = isEnabled();
        Color isi = null;
        Color garis = null;
        Color teks = Tema.TEKS;
        switch (jenis) {
            case UTAMA -> {
                isi = !nyala ? Tema.ABU : m.isPressed() ? Tema.PERMUKAAN_TERANG : m.isRollover() ? Tema.MARUN : Tema.MERAH_TUA;
                teks = nyala ? Tema.TEKS : Tema.TEKS_TBD;
            }
            case KEDUA -> {
                isi = !nyala ? null : m.isPressed() ? Tema.HITAM : m.isRollover() ? Tema.PERMUKAAN_TERANG : null;
                garis = nyala ? Tema.MERAH_TUA : Tema.ABU;
                teks = nyala ? Tema.TEKS : Tema.TEKS_TBD;
            }
            case MENU -> {
                isi = aktif ? Tema.MARUN : !nyala ? null : m.isRollover() ? Tema.PERMUKAAN_TERANG : null;
                teks = !nyala ? Tema.TEKS_TBD : aktif || m.isRollover() ? Tema.TEKS : Tema.TEKS_REDUP;
            }
        }

        int w = getWidth();
        int h = getHeight();
        if (isi != null) {
            g.setColor(isi);
            g.fillRoundRect(0, 0, w, h, 12, 12);
        }
        if (garis != null) {
            g.setColor(garis);
            g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
        }
        if (hasFocus() && nyala && lewatKeyboard) { // cincin fokus keyboard
            g.setColor(Tema.MERAH);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(1, 1, w - 3, h - 3, 12, 12);
        }

        g.setFont(jenis == Jenis.MENU && aktif ? getFont().deriveFont(java.awt.Font.BOLD) : getFont());
        FontMetrics fm = g.getFontMetrics();
        int x = getHorizontalAlignment() == SwingConstants.LEFT ? 14 : (w - fm.stringWidth(getText())) / 2;
        int y = (h + fm.getAscent() - fm.getDescent()) / 2;
        g.setColor(teks);
        g.drawString(getText(), x, y);
        g.dispose();
    }
}
