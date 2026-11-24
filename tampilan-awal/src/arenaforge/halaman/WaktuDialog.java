package arenaforge.halaman;

import arenaforge.Tema;
import arenaforge.ui.FormDialog;
import arenaforge.ui.Formulir;
import arenaforge.ui.Isian;
import arenaforge.ui.Pesan;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Window;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Dialog untuk mengubah waktu pertandingan. Format isian: yyyy-MM-dd HH:mm. */
class WaktuDialog extends FormDialog {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JTextField tfWaktu = Isian.teks(16);
    private LocalDateTime hasil; // tetap null kalau dibatalkan

    WaktuDialog(Window induk, String judulLaga, LocalDateTime sekarang) {
        super(induk, "Atur Waktu Pertandingan");
        if (sekarang != null) {
            tfWaktu.setText(FORMAT.format(sekarang));
        }
        Formulir form = new Formulir().tambah("Waktu", tfWaktu);
        JPanel isi = new JPanel(new BorderLayout(0, 8));
        isi.setOpaque(false);
        isi.setBorder(Tema.padding(18, 20, 4, 20));
        isi.add(Tema.label(judulLaga, java.awt.Font.BOLD, 15f), BorderLayout.NORTH);
        isi.add(form, BorderLayout.CENTER);
        isi.add(new TeksBungkus("Tulis dengan format tahun-bulan-tanggal jam:menit, contoh 2026-12-05 15:00.",
                12f, Tema.TEKS_REDUP, 320), BorderLayout.SOUTH);

        Tombol btnSimpan = new Tombol("Simpan", Tombol.Jenis.UTAMA);
        Tombol btnBatal = new Tombol("Batal", Tombol.Jenis.KEDUA);
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnSimpan);

        add(isi, BorderLayout.CENTER);
        add(barisTombol(btnBatal, btnSimpan), BorderLayout.SOUTH);
        selesaiDisusun();
    }

    LocalDateTime tampilkan() {
        setVisible(true);
        return hasil;
    }

    private void simpan() {
        try {
            hasil = LocalDateTime.parse(tfWaktu.getText().trim(), FORMAT);
            dispose();
        } catch (DateTimeParseException ex) {
            Pesan.peringatan(this, "Atur Waktu", "Format waktu belum benar. Contoh yang benar: 2026-12-05 15:00.");
            tfWaktu.requestFocusInWindow();
        }
    }
}
