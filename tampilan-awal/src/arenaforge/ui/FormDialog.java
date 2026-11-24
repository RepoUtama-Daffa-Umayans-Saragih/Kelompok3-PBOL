package arenaforge.ui;

import arenaforge.Tema;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JPanel;

/** Dasar semua dialog formulir: modal, latar hitam, ditutup lewat dispose(). */
public abstract class FormDialog extends JDialog {

    protected FormDialog(Window induk, String judul) {
        super(induk, judul, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Tema.HITAM);
        setLayout(new BorderLayout());
    }

    /** Baris tombol rata kanan di bagian bawah dialog. */
    protected JPanel barisTombol(Tombol... tombol) {
        JPanel baris = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        baris.setOpaque(false);
        baris.setBorder(Tema.padding(8, 20, 18, 20));
        for (Tombol t : tombol) {
            baris.add(t);
        }
        return baris;
    }

    /** Ukur, letakkan di tengah induk, lalu tampilkan. */
    protected void selesaiDisusun() {
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }
}
