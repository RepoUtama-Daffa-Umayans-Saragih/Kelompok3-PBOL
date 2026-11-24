package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Panel dengan sudut membulat dan bingkai tipis, dipakai sebagai kartu informasi. */
public class Kartu extends JPanel {
    private Color isi = Tema.PERMUKAAN;
    private Color garis = Tema.MARUN;
    private int lengkung = 20;

    public Kartu(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(Tema.padding(18, 20, 18, 20));
    }

    /** Ganti warna isi dan bingkai. Bingkai null berarti tanpa bingkai. */
    public Kartu warna(Color isi, Color garis) {
        this.isi = isi;
        this.garis = garis;
        repaint();
        return this;
    }

    /** Diameter lengkung sudut; semakin kecil semakin siku. */
    public Kartu lengkung(int lengkung) {
        this.lengkung = lengkung;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(isi);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), lengkung, lengkung);
        if (garis != null) {
            g.setColor(garis);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, lengkung, lengkung);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
