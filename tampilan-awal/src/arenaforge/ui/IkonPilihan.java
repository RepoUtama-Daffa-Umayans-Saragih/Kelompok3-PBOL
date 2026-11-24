package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.Icon;

/** Ikon kotak/bulat untuk JCheckBox dan JRadioButton yang cocok dengan tema gelap. */
public class IkonPilihan implements Icon {
    private static final int UKURAN = 18;
    private final boolean bulat;

    private IkonPilihan(boolean bulat) {
        this.bulat = bulat;
    }

    /** Pasang ikon bertema dan warna huruf yang benar ke satu checkbox atau radio button. */
    public static <T extends AbstractButton> T gayai(T tombol, boolean bulat) {
        tombol.setIcon(new IkonPilihan(bulat));
        tombol.setSelectedIcon(new IkonPilihan(bulat));
        tombol.setDisabledIcon(new IkonPilihan(bulat));
        tombol.setDisabledSelectedIcon(new IkonPilihan(bulat));
        tombol.setForeground(Tema.TEKS);
        tombol.setFont(Tema.font(java.awt.Font.PLAIN, 13f));
        tombol.setOpaque(false);
        tombol.setFocusPainted(false);
        tombol.setIconTextGap(8);
        tombol.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return tombol;
    }

    @Override
    public void paintIcon(Component c, Graphics g0, int x, int y) {
        AbstractButton tombol = (AbstractButton) c;
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean nyala = tombol.isEnabled();
        g.setColor(nyala ? (tombol.hasFocus() && Tombol.fokusKeyboard() ? Tema.MERAH : Tema.MERAH_TUA) : Tema.ABU);
        if (bulat) {
            g.drawOval(x, y, UKURAN - 1, UKURAN - 1);
        } else {
            g.drawRoundRect(x, y, UKURAN - 1, UKURAN - 1, 5, 5);
        }
        if (tombol.isSelected()) {
            g.setColor(nyala ? Tema.MERAH_TUA : Tema.ABU);
            if (bulat) {
                g.fillOval(x + 4, y + 4, UKURAN - 8, UKURAN - 8);
            } else {
                g.fillRoundRect(x + 4, y + 4, UKURAN - 8, UKURAN - 8, 3, 3);
            }
        }
        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return UKURAN;
    }

    @Override
    public int getIconHeight() {
        return UKURAN;
    }
}
