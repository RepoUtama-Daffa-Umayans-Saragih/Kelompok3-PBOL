package arenaforge.halaman;

import arenaforge.Tema;
import arenaforge.ui.Kartu;
import arenaforge.ui.TabelData;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Potongan susunan yang dipakai berulang oleh halaman-halaman Panitia dan Penonton. */
final class Kerangka {
    static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("EEE d MMM, HH.mm", Locale.forLanguageTag("id-ID"));

    private Kerangka() {
    }

    /** Dasar halaman: BorderLayout dengan jarak 16 dan ruang kosong di sekeliling. */
    static JPanel halaman() {
        JPanel halaman = new JPanel(new BorderLayout(0, 16));
        halaman.setBorder(Tema.padding(24, 28, 24, 28));
        return halaman;
    }

    /** Judul halaman di kiri dan tombol aksi (boleh null) di kanan. */
    static JPanel kepala(String judul, String penjelasan, JComponent aksi) {
        JPanel kepala = new JPanel(new BorderLayout(16, 0));
        kepala.setOpaque(false);
        kepala.add(Tema.kepalaHalaman(judul, penjelasan), BorderLayout.CENTER);
        if (aksi != null) {
            JPanel wadah = new JPanel(new BorderLayout());
            wadah.setOpaque(false);
            wadah.add(aksi, BorderLayout.NORTH);
            kepala.add(wadah, BorderLayout.EAST);
        }
        return kepala;
    }

    static JPanel barisTombol(Tombol... tombol) {
        JPanel baris = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        baris.setOpaque(false);
        for (Tombol t : tombol) {
            baris.add(t);
        }
        return baris;
    }

    /** Kartu berjudul yang membungkus isi apa pun. */
    static Kartu kartu(String judul, JComponent isi) {
        Kartu kartu = new Kartu(new BorderLayout(0, 10));
        kartu.setBorder(Tema.padding(14, 16, 14, 16));
        if (judul != null) {
            kartu.add(Tema.label(judul, Font.BOLD, 15f), BorderLayout.NORTH);
        }
        kartu.add(isi, BorderLayout.CENTER);
        return kartu;
    }

    /** Pesan untuk halaman yang belum ada isinya: apa sebabnya dan apa langkah berikutnya. */
    static Kartu kosong(String judul, String saran) {
        Kartu kosong = new Kartu(new BorderLayout(0, 4)).warna(Tema.PERMUKAAN, Tema.GARIS);
        kosong.add(Tema.label(judul, Font.BOLD, 14f), BorderLayout.NORTH);
        kosong.add(new arenaforge.ui.TeksBungkus(saran, 13f, Tema.TEKS_REDUP, 560), BorderLayout.CENTER);
        return kosong;
    }

    /** Tabel dengan kepalanya, tanpa scroll sendiri, tingginya pas dengan jumlah baris. */
    static JPanel tabelPas(TabelData tabel) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width,
                        tabel.getTableHeader().getPreferredSize().height + tabel.getRowCount() * tabel.getRowHeight());
            }
        };
        panel.setOpaque(false);
        panel.add(tabel.getTableHeader(), BorderLayout.NORTH);
        panel.add(tabel, BorderLayout.CENTER);
        return panel;
    }
}
