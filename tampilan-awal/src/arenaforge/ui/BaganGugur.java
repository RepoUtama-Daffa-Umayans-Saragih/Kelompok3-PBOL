package arenaforge.ui;

import arenaforge.Tema;
import arenaforge.model.KnockoutMatch;
import arenaforge.model.Team;
import arenaforge.model.Tournament;
import java.awt.BasicStroke;
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
 * Bagan gugur yang digambar sendiri (Graphics2D): satu kolom per babak, kotak laga berisi dua team dan
 * skor, garis penghubung ke babak berikutnya. Pemenang dicetak tebal di latar marun.
 */
public class BaganGugur extends JComponent {
    private static final int LEBAR_KOTAK = 210;
    private static final int TINGGI_BARIS = 26;
    private static final int TINGGI_KOTAK = TINGGI_BARIS * 2;
    private static final int JARAK_KOLOM = 56;
    private static final int JARAK_LAGA = 22;
    private static final int ATAS = 34;
    private static final int PINGGIR = 8;

    private Tournament turnamen;

    public BaganGugur(Tournament turnamen) {
        this.turnamen = turnamen;
        setOpaque(true);
        setBackground(Tema.HITAM);
    }

    private List<List<KnockoutMatch>> bagan() {
        return turnamen.getBagan();
    }

    /** Titik tengah vertikal kotak laga ke-i di babak ke-r. */
    private int pusatY(int r, int i) {
        if (r == 0) {
            return ATAS + TINGGI_KOTAK / 2 + i * (TINGGI_KOTAK + JARAK_LAGA);
        }
        return (pusatY(r - 1, 2 * i) + pusatY(r - 1, 2 * i + 1)) / 2;
    }

    @Override
    public Dimension getPreferredSize() {
        if (bagan().isEmpty()) {
            return new Dimension(200, 100);
        }
        int kolom = bagan().size();
        int baris = bagan().get(0).size();
        int lebar = PINGGIR * 2 + kolom * LEBAR_KOTAK + (kolom - 1) * JARAK_KOLOM;
        int tinggi = ATAS + baris * TINGGI_KOTAK + (baris - 1) * JARAK_LAGA + 56;
        return new Dimension(lebar, tinggi);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        List<List<KnockoutMatch>> bagan = bagan();
        for (int r = 0; r < bagan.size(); r++) {
            int x = PINGGIR + r * (LEBAR_KOTAK + JARAK_KOLOM);
            g.setFont(Tema.font(Font.BOLD, 12f));
            g.setColor(Tema.TEKS_REDUP);
            g.drawString(bagan.get(r).get(0).namaFase().toUpperCase(), x, 18);

            for (int i = 0; i < bagan.get(r).size(); i++) {
                int y = pusatY(r, i) - TINGGI_KOTAK / 2;
                // Garis penghubung ke kotak di babak berikutnya
                if (r + 1 < bagan.size()) {
                    int yTujuan = pusatY(r + 1, i / 2);
                    int xAkhir = x + LEBAR_KOTAK;
                    int xTengah = xAkhir + JARAK_KOLOM / 2;
                    g.setColor(Tema.MARUN);
                    g.setStroke(new BasicStroke(2f));
                    g.drawLine(xAkhir, pusatY(r, i), xTengah, pusatY(r, i));
                    g.drawLine(xTengah, pusatY(r, i), xTengah, yTujuan);
                    g.drawLine(xTengah, yTujuan, xAkhir + JARAK_KOLOM, yTujuan);
                }
                gambarKotak(g, bagan.get(r).get(i), x, y, r == bagan.size() - 1);
            }
        }

        Team juara = turnamen.getJuara();
        if (juara != null) {
            int r = bagan.size() - 1;
            int x = PINGGIR + r * (LEBAR_KOTAK + JARAK_KOLOM);
            int y = pusatY(r, 0) + TINGGI_KOTAK / 2 + 26;
            g.setFont(Tema.font(Font.BOLD, 15f));
            g.setColor(Tema.MERAH);
            g.drawString("Juara: " + juara.getNama(), x, y);
        }
        g.dispose();
    }

    private void gambarKotak(Graphics2D g, KnockoutMatch m, int x, int y, boolean final_) {
        g.setColor(Tema.PERMUKAAN);
        g.fillRoundRect(x, y, LEBAR_KOTAK, TINGGI_KOTAK, 10, 10);
        g.setColor(final_ ? Tema.MERAH : Tema.MARUN);
        g.setStroke(new BasicStroke(final_ ? 2f : 1f));
        g.drawRoundRect(x, y, LEBAR_KOTAK, TINGGI_KOTAK, 10, 10);

        Team pemenang = m.tentukanPemenang();
        gambarBaris(g, x, y, m.namaA(), m.getTeamA() != null, m.isSelesai() ? m.getSkorA() : null,
                pemenang != null && pemenang == m.getTeamA(), pemenang != null && pemenang != m.getTeamA(), true);
        gambarBaris(g, x, y + TINGGI_BARIS, m.namaB(), m.getTeamB() != null, m.isSelesai() ? m.getSkorB() : null,
                pemenang != null && pemenang == m.getTeamB(), pemenang != null && pemenang != m.getTeamB(), false);
        g.setColor(Tema.GARIS);
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x + 1, y + TINGGI_BARIS, x + LEBAR_KOTAK - 1, y + TINGGI_BARIS);
    }

    private void gambarBaris(Graphics2D g, int x, int y, String nama, boolean pasti, Integer skor,
                             boolean menang, boolean kalah, boolean atas) {
        if (menang) {
            g.setColor(Tema.MARUN);
            if (atas) {
                g.fillRoundRect(x + 1, y + 1, LEBAR_KOTAK - 2, TINGGI_BARIS + 8, 10, 10);
                g.setColor(Tema.PERMUKAAN);
                g.fillRect(x + 1, y + TINGGI_BARIS, LEBAR_KOTAK - 2, 10);
                g.setColor(Tema.MARUN);
                g.fillRect(x + 1, y + 1 + 8, LEBAR_KOTAK - 2, TINGGI_BARIS - 8);
            } else {
                g.fillRoundRect(x + 1, y - 7, LEBAR_KOTAK - 2, TINGGI_BARIS + 6, 10, 10);
                g.setColor(Tema.PERMUKAAN);
                g.fillRect(x + 1, y - 7, LEBAR_KOTAK - 2, 8);
                g.setColor(Tema.MARUN);
                g.fillRect(x + 1, y, LEBAR_KOTAK - 2, TINGGI_BARIS - 1);
            }
        }
        int gaya = menang ? Font.BOLD : pasti ? Font.PLAIN : Font.ITALIC;
        g.setFont(Tema.font(gaya, 13f));
        FontMetrics fm = g.getFontMetrics();
        Color warna = menang ? Tema.TEKS : kalah ? Tema.TEKS_TBD : pasti ? Tema.TEKS : Tema.TEKS_TBD;
        g.setColor(warna);
        int yTeks = y + (TINGGI_BARIS + fm.getAscent() - fm.getDescent()) / 2;
        String teks = nama;
        int lebarTersedia = LEBAR_KOTAK - 48;
        while (fm.stringWidth(teks) > lebarTersedia && teks.length() > 3) {
            teks = teks.substring(0, teks.length() - 2) + "…";
        }
        g.drawString(teks, x + 12, yTeks);
        if (skor != null) {
            g.setFont(Tema.font(Font.BOLD, 14f));
            String s = String.valueOf(skor);
            g.setColor(menang ? Tema.TEKS : Tema.TEKS_REDUP);
            g.drawString(s, x + LEBAR_KOTAK - 16 - g.getFontMetrics().stringWidth(s) / 2, yTeks);
        }
    }
}
