package arenaforge.ui;

import arenaforge.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** Dialog pesan dan konfirmasi bertema ArenaForge, pengganti JOptionPane. */
public final class Pesan {
    private Pesan() {
    }

    public static void info(Component induk, String judul, String teks) {
        tampil(induk, judul, teks, Tema.MARUN, false);
    }

    public static void peringatan(Component induk, String judul, String teks) {
        tampil(induk, judul, teks, Tema.MERAH_TUA, false);
    }

    public static void galat(Component induk, String judul, String teks) {
        tampil(induk, judul, teks, Tema.MERAH, false);
    }

    /** Pertanyaan Ya/Tidak. Hasilnya true kalau pengguna memilih Ya. */
    public static boolean konfirmasi(Component induk, String judul, String teks) {
        return tampil(induk, judul, teks, Tema.MARUN, true);
    }

    private static boolean tampil(Component induk, String judul, String teks, Color aksen, boolean tanya) {
        Window pemilik = induk instanceof Window w ? w : induk == null ? null : SwingUtilities.getWindowAncestor(induk);
        JDialog dialog = new JDialog(pemilik, judul, Dialog.ModalityType.APPLICATION_MODAL);
        boolean[] hasil = {false};

        Tombol btnYa = new Tombol(tanya ? "Ya" : "OK", Tombol.Jenis.UTAMA);
        btnYa.addActionListener(e -> {
            hasil[0] = true;
            dialog.dispose();
        });
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        if (tanya) {
            Tombol btnTidak = new Tombol("Tidak", Tombol.Jenis.KEDUA);
            btnTidak.addActionListener(e -> dialog.dispose());
            tombol.add(btnTidak);
        }
        tombol.add(btnYa);

        JPanel isi = new JPanel(new BorderLayout(0, 16));
        isi.setOpaque(false);
        isi.setBorder(Tema.padding(20, 22, 16, 22));
        isi.add(Tema.label(judul, Font.BOLD, 16f), BorderLayout.NORTH);
        isi.add(new TeksBungkus(teks, 13f, Tema.TEKS, 380), BorderLayout.CENTER);
        isi.add(tombol, BorderLayout.SOUTH);

        JPanel strip = new JPanel();
        strip.setBackground(aksen);
        strip.setPreferredSize(new java.awt.Dimension(6, 0));

        JPanel akar = new JPanel(new BorderLayout());
        akar.setBackground(Tema.PERMUKAAN);
        akar.add(strip, BorderLayout.WEST);
        akar.add(isi, BorderLayout.CENTER);
        dialog.setContentPane(akar);

        // Escape menutup dialog (sama dengan "Tidak")
        akar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "tutup");
        akar.getActionMap().put("tutup", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        dialog.getRootPane().setDefaultButton(btnYa);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(460, 10);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(pemilik);
        dialog.setVisible(true);
        return hasil[0];
    }
}
