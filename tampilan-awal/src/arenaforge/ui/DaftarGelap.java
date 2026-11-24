package arenaforge.ui;

import arenaforge.Tema;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.function.Function;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/** JList gelap; tiap item bisa punya judul dan satu baris keterangan kecil di bawahnya. */
public class DaftarGelap<E> extends JList<E> {

    public DaftarGelap(ListModel<E> model, Function<E, String> judul, Function<E, String> keterangan) {
        super(model);
        mulai(judul, keterangan);
    }

    public DaftarGelap(Function<E, String> judul, Function<E, String> keterangan) {
        super(new DefaultListModel<>());
        mulai(judul, keterangan);
    }

    @SuppressWarnings("unchecked")
    public DefaultListModel<E> modelIsi() {
        return (DefaultListModel<E>) getModel();
    }

    private void mulai(Function<E, String> judul, Function<E, String> keterangan) {
        setBackground(Tema.PERMUKAAN);
        setForeground(Tema.TEKS);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFixedCellHeight(-1);
        setCellRenderer(new ListCellRenderer<E>() {
            private final JPanel sel = new JPanel(new BorderLayout(0, 1));
            private final JLabel lblJudul = Tema.label("", Font.BOLD, 14f);
            private final JLabel lblKet = Tema.redup("", Font.PLAIN, 12f);

            {
                sel.setBorder(Tema.padding(8, 12, 8, 12));
                sel.add(lblJudul, BorderLayout.NORTH);
                sel.add(lblKet, BorderLayout.SOUTH);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends E> list, E nilai, int indeks,
                                                          boolean dipilih, boolean fokus) {
                lblJudul.setText(judul.apply(nilai));
                String ket = keterangan == null ? "" : keterangan.apply(nilai);
                lblKet.setText(ket);
                lblKet.setVisible(!ket.isEmpty());
                sel.setBackground(dipilih ? Tema.MARUN : Tema.PERMUKAAN);
                lblKet.setForeground(dipilih ? Tema.TEKS : Tema.TEKS_REDUP);
                return sel;
            }
        });
    }
}
