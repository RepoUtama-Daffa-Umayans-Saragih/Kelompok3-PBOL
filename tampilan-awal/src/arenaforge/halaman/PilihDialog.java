package arenaforge.halaman;

import arenaforge.Tema;
import arenaforge.ui.DaftarGelap;
import arenaforge.ui.FormDialog;
import arenaforge.ui.Pesan;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;
import java.util.function.Function;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/** Dialog memilih satu atau beberapa item dari daftar, dipakai untuk memilih player dan team. */
class PilihDialog<E> extends FormDialog {
    private final DaftarGelap<E> daftar;
    private List<E> hasil = List.of(); // tetap kosong kalau dibatalkan

    PilihDialog(Window induk, String judul, String petunjuk, List<E> isi,
                Function<E, String> teks, Function<E, String> keterangan, boolean banyak) {
        super(induk, judul);
        daftar = new DaftarGelap<>(teks, keterangan);
        isi.forEach(daftar.modelIsi()::addElement);
        daftar.setSelectionMode(banyak ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

        JScrollPane gulir = Tema.gulir(daftar);
        gulir.setPreferredSize(new Dimension(380, 280));
        gulir.getViewport().setBackground(Tema.PERMUKAAN);
        gulir.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel isiPanel = new JPanel(new BorderLayout(0, 10));
        isiPanel.setOpaque(false);
        isiPanel.setBorder(Tema.padding(18, 20, 4, 20));
        isiPanel.add(new TeksBungkus(petunjuk, 13f, Tema.TEKS_REDUP, 380), BorderLayout.NORTH);
        isiPanel.add(gulir, BorderLayout.CENTER);

        Tombol btnPilih = new Tombol("Pilih", Tombol.Jenis.UTAMA);
        Tombol btnBatal = new Tombol("Batal", Tombol.Jenis.KEDUA);
        btnPilih.addActionListener(e -> pilih());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnPilih);

        add(isiPanel, BorderLayout.CENTER);
        add(barisTombol(btnBatal, btnPilih), BorderLayout.SOUTH);
        selesaiDisusun();
    }

    List<E> tampilkan() {
        setVisible(true);
        return hasil;
    }

    private void pilih() {
        if (daftar.isSelectionEmpty()) {
            Pesan.peringatan(this, getTitle(), "Pilih dulu minimal satu dari daftar.");
            return;
        }
        hasil = daftar.getSelectedValuesList();
        dispose();
    }
}
