package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/** JComboBox bertema gelap: panah merah, daftar hitam, pilihan marun. */
public class ComboGelap<E> extends JComboBox<E> {

    public ComboGelap(E[] isi) {
        super(isi);
        mulai();
    }

    public ComboGelap(ComboBoxModel<E> model) {
        super(model);
        mulai();
    }

    public ComboGelap() {
        super();
        mulai();
    }

    private void mulai() {
        setUI(new UiCombo());
        setBackground(Tema.PERMUKAAN);
        setForeground(Tema.TEKS);
        setFont(Tema.font(java.awt.Font.PLAIN, 14f));
        setBorder(BorderFactory.createLineBorder(Tema.MARUN));
        setOpaque(true);
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object nilai, int indeks,
                                                          boolean dipilih, boolean fokus) {
                super.getListCellRendererComponent(list, nilai, indeks, dipilih, false);
                setBackground(dipilih ? Tema.MARUN : Tema.PERMUKAAN);
                setForeground(Tema.TEKS);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return this;
            }
        });
    }

    @Override
    public void updateUI() {
        // Jangan kembali ke UI bawaan kalau tema diganti
        setUI(new UiCombo());
    }

    private static final class UiCombo extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton tombol = new JButton() {
                @Override
                protected void paintComponent(Graphics g0) {
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(Tema.PERMUKAAN);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Tema.MERAH_TUA);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    g.fillPolygon(new int[] {cx - 5, cx + 5, cx}, new int[] {cy - 2, cy - 2, cy + 3}, 3);
                    g.dispose();
                }
            };
            tombol.setBorder(BorderFactory.createEmptyBorder());
            tombol.setPreferredSize(new Dimension(26, 0));
            tombol.setFocusable(false);
            return tombol;
        }

        @Override
        protected ComboPopup createPopup() {
            return new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane gulir = GulirGelap.bungkus(list);
                    gulir.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    return gulir;
                }

                @Override
                public void show() {
                    setBorder(BorderFactory.createLineBorder(Tema.MARUN));
                    super.show();
                }
            };
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle batas, boolean fokus) {
            g.setColor(Tema.PERMUKAAN);
            g.fillRect(batas.x, batas.y, batas.width, batas.height);
        }
    }
}
