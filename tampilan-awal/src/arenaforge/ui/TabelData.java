package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntPredicate;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** JTable hanya-baca bertema gelap: baris berselang-seling, pilihan marun, dan baris yang bisa disorot. */
public class TabelData extends JTable {
    private final DefaultTableModel model;
    private final Set<Integer> kolomTengah = new HashSet<>();
    private IntPredicate sorot = baris -> false;

    public TabelData(String... kolom) {
        super(new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        });
        this.model = (DefaultTableModel) getModel();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(32);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        setBackground(Tema.PERMUKAAN);
        setForeground(Tema.TEKS);
        setSelectionBackground(Tema.MARUN);
        setSelectionForeground(Tema.TEKS);
        setFont(Tema.font(Font.PLAIN, 13f));
        setFocusable(true);

        DefaultTableCellRenderer sel = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object nilai, boolean dipilih, boolean fokus,
                                                           int baris, int kolom) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, nilai, dipilih, false, baris, kolom);
                boolean disorot = sorot.test(baris);
                l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                l.setHorizontalAlignment(kolomTengah.contains(kolom) ? SwingConstants.CENTER : SwingConstants.LEFT);
                l.setFont(Tema.font(disorot ? Font.BOLD : Font.PLAIN, 13f));
                Color latar = baris % 2 == 0 ? Tema.PERMUKAAN : Tema.HITAM;
                l.setBackground(dipilih ? Tema.MARUN : latar);
                l.setForeground(dipilih || disorot ? Tema.TEKS : Tema.TEKS_REDUP.brighter());
                if (!dipilih && disorot) {
                    l.setBackground(Tema.PERMUKAAN_TERANG);
                }
                return l;
            }
        };
        setDefaultRenderer(Object.class, sel);
        setDefaultRenderer(String.class, sel);
        setDefaultRenderer(Integer.class, sel);

        JTableHeader kepala = getTableHeader();
        kepala.setReorderingAllowed(false);
        kepala.setPreferredSize(new Dimension(0, 34));
        kepala.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object nilai, boolean dipilih, boolean fokus,
                                                           int baris, int kolom) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, nilai, false, false, baris, kolom);
                l.setBackground(Tema.PERMUKAAN_TERANG);
                l.setForeground(Tema.TEKS_REDUP);
                l.setFont(Tema.font(Font.BOLD, 12f));
                l.setHorizontalAlignment(kolomTengah.contains(kolom) ? SwingConstants.CENTER : SwingConstants.LEFT);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, Tema.MARUN),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                l.setOpaque(true);
                return l;
            }
        });
    }

    public TabelData ratakanTengah(int... kolom) {
        for (int k : kolom) {
            kolomTengah.add(k);
        }
        return this;
    }

    public TabelData lebarKolom(int kolom, int lebar) {
        getColumnModel().getColumn(kolom).setPreferredWidth(lebar);
        return this;
    }

    /** Baris yang memenuhi syarat ini dicetak tebal dan dilatari lebih terang (mis. tim yang lolos). */
    public void setSorot(IntPredicate sorot) {
        this.sorot = sorot;
        repaint();
    }

    public void kosongkan() {
        model.setRowCount(0);
    }

    public void tambah(Object... baris) {
        model.addRow(baris);
    }
}
