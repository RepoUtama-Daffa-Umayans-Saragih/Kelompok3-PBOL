package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Tournament;
import arenaforge.ui.ComboGelap;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Dasar halaman yang bekerja pada satu turnamen yang dipilih lewat combo di atas
 * (Jadwal & Bracket, Input Skor, Klasemen). Subclass mengisi isi halaman lewat tampilkan().
 */
abstract class HalamanTurnamen extends JPanel {
    private final JPanel konten = new JPanel(new BorderLayout());
    private final String judul;
    private final String penjelasan;
    private Tournament dipilih;

    protected HalamanTurnamen(String judul, String penjelasan) {
        super(new BorderLayout(0, 16));
        this.judul = judul;
        this.penjelasan = penjelasan;
        setBorder(Tema.padding(24, 28, 24, 28));
    }

    /** Dipanggil subclass di akhir konstruktornya, setelah semua field-nya siap. */
    protected final void mulai() {
        JComponent pemilih = buatPemilih();
        add(Kerangka.kepala(judul, penjelasan, pemilih), BorderLayout.NORTH);
        konten.setOpaque(false);
        add(konten, BorderLayout.CENTER);

        if (DataSementara.TURNAMEN.isEmpty()) {
            konten.add(Kerangka.kosong("Belum ada turnamen.",
                    "Panitia membuat turnamen dan menyusun jadwalnya di menu Turnamen."), BorderLayout.NORTH);
        } else {
            ubah(DataSementara.TURNAMEN.get(0));
        }
    }

    private JComponent buatPemilih() {
        if (DataSementara.TURNAMEN.isEmpty()) {
            return null;
        }
        Tournament[] semua = new Tournament[DataSementara.TURNAMEN.size()];
        DataSementara.TURNAMEN.copyInto(semua);
        ComboGelap<Tournament> combo = new ComboGelap<>(semua);
        // Event: ganti turnamen -> isi halaman ikut berganti
        combo.addActionListener(e -> ubah((Tournament) combo.getSelectedItem()));

        JPanel wadah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        wadah.setOpaque(false);
        wadah.add(Tema.label("Turnamen", Font.BOLD, 13f));
        wadah.add(combo);
        return wadah;
    }

    private void ubah(Tournament t) {
        dipilih = t;
        muatUlang();
    }

    protected final Tournament getDipilih() {
        return dipilih;
    }

    /** Membangun ulang isi halaman dari data turnamen yang sedang dipilih. */
    protected final void muatUlang() {
        konten.removeAll();
        tampilkan(dipilih, konten);
        konten.revalidate();
        konten.repaint();
    }

    /** Isi wadah (BorderLayout) dengan tampilan untuk turnamen ini. */
    protected abstract void tampilkan(Tournament t, JPanel wadah);
}
