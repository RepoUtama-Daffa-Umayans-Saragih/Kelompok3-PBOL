package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** JScrollPane bertema gelap: scrollbar tipis dengan jempol marun. */
public final class GulirGelap {
    private GulirGelap() {
    }

    public static JScrollPane bungkus(Component isi) {
        JScrollPane gulir = new JScrollPane(isi);
        gulir.setBorder(BorderFactory.createEmptyBorder());
        gulir.getViewport().setBackground(Tema.HITAM);
        gulir.setBackground(Tema.HITAM);
        gulir.getVerticalScrollBar().setUI(new UiScrollbar());
        gulir.getHorizontalScrollBar().setUI(new UiScrollbar());
        gulir.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        gulir.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        gulir.getVerticalScrollBar().setUnitIncrement(16);
        gulir.getHorizontalScrollBar().setUnitIncrement(16);
        return gulir;
    }

    private static final class UiScrollbar extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return tombolKosong();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return tombolKosong();
        }

        private static JButton tombolKosong() {
            JButton tombol = new JButton();
            tombol.setPreferredSize(new Dimension(0, 0));
            tombol.setBorder(null);
            return tombol;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle batas) {
            g.setColor(Tema.HITAM);
            g.fillRect(batas.x, batas.y, batas.width, batas.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle batas) {
            if (batas.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color warna = isDragging || isThumbRollover() ? Tema.MERAH_TUA : Tema.MARUN;
            g2.setColor(warna);
            g2.fillRoundRect(batas.x + 1, batas.y + 1, batas.width - 2, batas.height - 2, 8, 8);
            g2.dispose();
        }
    }

    /** Dipakai kalau ada JScrollBar lepas yang perlu tema yang sama. */
    public static void gayai(JScrollBar bar) {
        bar.setUI(new UiScrollbar());
    }
}
