package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JTextArea;

/**
 * Teks yang otomatis turun baris, dipakai sebagai pengganti JLabel berformat HTML.
 * Lebarnya mengikuti induk tetapi tidak melebihi lebarMaks.
 */
public class TeksBungkus extends JTextArea {
    private final int lebarMaks;
    private final ComponentAdapter pengamat = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            revalidate();
        }
    };

    public TeksBungkus(String teks, float ukuran, Color warna, int lebarMaks) {
        super(teks);
        this.lebarMaks = lebarMaks;
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(null);
        setFont(Tema.font(Font.PLAIN, ukuran));
        setForeground(warna);
    }

    public TeksBungkus(String teks, float ukuran, Color warna) {
        this(teks, ukuran, warna, 640);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (getParent() != null) {
            getParent().addComponentListener(pengamat);
        }
    }

    @Override
    public void removeNotify() {
        if (getParent() != null) {
            getParent().removeComponentListener(pengamat);
        }
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() {
        int lebarInduk = getParent() == null ? lebarMaks : getParent().getWidth();
        int lebar = lebarInduk <= 0 ? lebarMaks : Math.min(lebarMaks, lebarInduk);
        setSize(lebar, Short.MAX_VALUE);
        Dimension d = super.getPreferredSize();
        return new Dimension(lebar, d.height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(lebarMaks, getPreferredSize().height);
    }
}
