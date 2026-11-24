package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/** Label status kecil: LOLOS (merah tua), GRUP (hanya bingkai), GUGUR (abu). */
public class Lencana extends JComponent {
    public enum Jenis { LOLOS, GRUP, GUGUR }

    private final String teks;
    private final Jenis jenis;

    public Lencana(String teks, Jenis jenis) {
        this.teks = teks;
        this.jenis = jenis;
        setFont(Tema.font(Font.BOLD, 12f));
        setOpaque(false);
    }

    /** Pilih jenis lencana dari teks status tim, mis. "Lolos ke Semifinal" atau "Tersingkir di fase grup". */
    public static Lencana dariStatus(String status) {
        Jenis jenis = status.startsWith("Tersingkir") ? Jenis.GUGUR
                : status.startsWith("Fase grup") || status.startsWith("Menunggu") ? Jenis.GRUP : Jenis.LOLOS;
        return new Lencana(status, jenis);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(teks) + 20, fm.getHeight() + 6);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Color warnaTeks = Tema.TEKS;
        if (jenis == Jenis.LOLOS) {
            g.setColor(Tema.MERAH_TUA);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        } else if (jenis == Jenis.GRUP) {
            g.setColor(Tema.MERAH_TUA);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        } else {
            g.setColor(Tema.ABU);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            warnaTeks = Tema.TEKS_REDUP;
        }
        FontMetrics fm = g.getFontMetrics(getFont());
        g.setFont(getFont());
        g.setColor(warnaTeks);
        g.drawString(teks, 10, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }
}
