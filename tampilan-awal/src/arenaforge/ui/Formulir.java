package arenaforge.ui;

import arenaforge.Tema;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Panel formulir dua kolom: label di kiri, isian di kanan (GridBagLayout). */
public class Formulir extends JPanel {
    private int baris;

    public Formulir() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    public Formulir tambah(String judul, JComponent isian) {
        GridBagConstraints kiri = new GridBagConstraints();
        kiri.gridx = 0;
        kiri.gridy = baris;
        kiri.anchor = GridBagConstraints.WEST;
        kiri.insets = new Insets(6, 0, 6, 16);
        JLabel label = Tema.label(judul, java.awt.Font.BOLD, 13f);
        label.setLabelFor(isian);
        add(label, kiri);

        GridBagConstraints kanan = new GridBagConstraints();
        kanan.gridx = 1;
        kanan.gridy = baris;
        kanan.weightx = 1;
        kanan.fill = GridBagConstraints.HORIZONTAL;
        kanan.insets = new Insets(6, 0, 6, 0);
        add(isian, kanan);
        baris++;
        return this;
    }

    /** Satu komponen selebar dua kolom (mis. daftar atau keterangan). */
    public Formulir tambahLebar(JComponent isi) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = baris++;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        add(isi, c);
        return this;
    }
}
