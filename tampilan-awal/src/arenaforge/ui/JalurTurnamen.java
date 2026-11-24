package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JComponent;

/**
 * Jalur turnamen dari fase grup sampai final. Tahap yang sudah dicapai menyala mengikuti urutan
 * palet (marun, merah tua, merah), jadi makin dekat ke final makin merah.
 */
public class JalurTurnamen extends JComponent {
    private final List<String> tahapan;
    private final int sekarang;

    public JalurTurnamen(List<String> tahapan, int sekarang) {
        this.tahapan = tahapan;
        this.sekarang = sekarang;
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(240, 34);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 34);
    }

    /** 0 = marun, 1 = merah tua, 2 = merah. Tahap terakhir (final) selalu merah. */
    static Color warnaNyala(int indeks, int jumlah) {
        int tingkat = jumlah <= 1 ? 2 : (int) Math.round(indeks * 2.0 / (jumlah - 1));
        return switch (tingkat) {
            case 0 -> Tema.MARUN;
            case 1 -> Tema.MERAH_TUA;
            default -> Tema.MERAH;
        };
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int n = tahapan.size();
        int jarak = 4;
        int lebar = (getWidth() - jarak * (n - 1)) / n;
        for (int i = 0; i < n; i++) {
            int x = i * (lebar + jarak);
            g.setColor(i <= sekarang ? warnaNyala(i, n) : Tema.PADAM);
            g.fillRoundRect(x, 0, lebar, 6, 4, 4);

            String nama = tahapan.get(i).equals("Fase Grup") ? "Grup" : tahapan.get(i);
            g.setFont(Tema.font(i == sekarang ? Font.BOLD : Font.PLAIN, 11f));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(i > sekarang ? Tema.TEKS_TBD : Tema.TEKS);
            g.drawString(nama, x, 6 + 6 + fm.getAscent());
        }
        g.dispose();
    }
}
