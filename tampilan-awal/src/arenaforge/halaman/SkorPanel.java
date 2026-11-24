package arenaforge.halaman;

import arenaforge.MainFrame;
import arenaforge.Tema;
import arenaforge.model.AturanTurnamenException;
import arenaforge.model.GroupMatch;
import arenaforge.model.Match;
import arenaforge.model.Team;
import arenaforge.model.Tournament;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.Kartu;
import arenaforge.ui.Pesan;
import arenaforge.ui.TabelData;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

/**
 * Input Skor (khusus panitia): memilih pertandingan, mengisi skor, dan mengatur waktunya.
 * Aturan skor (Bo2 fase grup, Bo3 babak gugur) diperiksa oleh class Match, bukan oleh layar ini.
 */
public class SkorPanel extends HalamanTurnamen {
    private final MainFrame frame;
    private final TabelData tabel = new TabelData("Fase", "Pertandingan", "Waktu", "Skor", "Status")
            .ratakanTengah(3).lebarKolom(0, 90).lebarKolom(1, 260).lebarKolom(2, 160).lebarKolom(3, 70).lebarKolom(4, 150);
    private final JLabel lblLaga = Tema.label("Pilih pertandingan di tabel.", Font.BOLD, 15f);
    private final JLabel lblA = Tema.label("-", Font.PLAIN, 13f);
    private final JLabel lblB = Tema.label("-", Font.PLAIN, 13f);
    private final ComboGelap<Integer> cbSkorA = new ComboGelap<>(new Integer[] {0, 1, 2});
    private final ComboGelap<Integer> cbSkorB = new ComboGelap<>(new Integer[] {0, 1, 2});
    private final Tombol btnSimpan = new Tombol("Simpan Skor", Tombol.Jenis.UTAMA);
    private final Tombol btnWaktu = new Tombol("Atur Waktu", Tombol.Jenis.KEDUA);
    private final TeksBungkus lblFormat = new TeksBungkus("", 12f, Tema.TEKS_REDUP, 520);
    private List<Match> daftarMatch = List.of();

    public SkorPanel(MainFrame frame) {
        super("Input Skor", "Catat skor pertandingan. Pemenang, klasemen, bagan, dan statistik player diperbarui otomatis.");
        this.frame = frame;
        btnSimpan.addActionListener(e -> simpanSkor());
        btnWaktu.addActionListener(e -> aturWaktu());
        tabel.getSelectionModel().addListSelectionListener(this::pilihBaris);
        mulai();
    }

    @Override
    protected void tampilkan(Tournament t, JPanel wadah) {
        if (!t.isJadwalDisusun()) {
            wadah.add(Kerangka.kosong("Jadwal " + t.getNama() + " belum disusun.",
                    "Susun jadwal di menu Turnamen setelah kuota penuh, lalu skor bisa dicatat di sini."), BorderLayout.NORTH);
            return;
        }
        daftarMatch = t.semuaMatch();
        isiTabel();
        pilihAwal();

        JScrollPane gulir = Tema.gulir(tabel);
        gulir.setViewportBorder(null);
        gulir.getViewport().setBackground(Tema.PERMUKAAN);

        JPanel formSkor = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        formSkor.setOpaque(false);
        formSkor.add(lblA);
        formSkor.add(cbSkorA);
        formSkor.add(Tema.label("-", Font.BOLD, 16f));
        formSkor.add(cbSkorB);
        formSkor.add(lblB);
        formSkor.add(btnSimpan);
        formSkor.add(btnWaktu);

        JPanel atas = new JPanel(new BorderLayout(0, 8));
        atas.setOpaque(false);
        atas.add(lblLaga, BorderLayout.NORTH);
        atas.add(formSkor, BorderLayout.CENTER);
        atas.add(lblFormat, BorderLayout.SOUTH);
        Kartu kartuForm = new Kartu(new BorderLayout()).warna(Tema.PERMUKAAN, Tema.MARUN);
        kartuForm.setBorder(Tema.padding(14, 16, 14, 16));
        kartuForm.add(atas, BorderLayout.CENTER);

        wadah.add(gulir, BorderLayout.CENTER);
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.setBorder(Tema.padding(14, 0, 0, 0));
        bawah.add(kartuForm, BorderLayout.CENTER);
        wadah.add(bawah, BorderLayout.SOUTH);
        aturForm(terpilih());
    }

    private void isiTabel() {
        int terpilih = tabel.getSelectedRow();
        tabel.kosongkan();
        for (Match m : daftarMatch) {
            String status = m.isSelesai() ? "Selesai" : m.siapDimainkan() ? "Belum dimainkan" : "Menunggu lawan";
            String fase = m instanceof GroupMatch g ? g.getGrup() : m.namaFase();
            tabel.tambah(fase, m.namaA() + " vs " + m.namaB(), Kerangka.WAKTU.format(m.getWaktu()), m.teksSkor(), status);
        }
        if (terpilih >= 0 && terpilih < tabel.getRowCount()) {
            tabel.setRowSelectionInterval(terpilih, terpilih);
        }
    }

    /** Langsung memilih pertandingan pertama yang siap dimainkan tapi belum ada skornya. */
    private void pilihAwal() {
        int baris = 0;
        for (int i = 0; i < daftarMatch.size(); i++) {
            if (daftarMatch.get(i).siapDimainkan() && !daftarMatch.get(i).isSelesai()) {
                baris = i;
                break;
            }
        }
        if (!daftarMatch.isEmpty()) {
            tabel.setRowSelectionInterval(baris, baris);
            tabel.scrollRectToVisible(tabel.getCellRect(baris, 0, true));
        }
    }

    private Match terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 || baris >= daftarMatch.size() ? null : daftarMatch.get(baris);
    }

    private void pilihBaris(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            aturForm(terpilih());
        }
    }

    /** Menyesuaikan isi form dengan pertandingan yang dipilih. */
    private void aturForm(Match m) {
        boolean ada = m != null;
        btnWaktu.setEnabled(ada);
        btnSimpan.setEnabled(ada && m.siapDimainkan());
        cbSkorA.setEnabled(ada && m.siapDimainkan());
        cbSkorB.setEnabled(ada && m.siapDimainkan());
        if (!ada) {
            lblLaga.setText("Pilih pertandingan di tabel.");
            lblA.setText("-");
            lblB.setText("-");
            lblFormat.setText("");
            return;
        }
        lblLaga.setText(m.namaFase() + (m instanceof GroupMatch g ? ", " + g.getGrup() : "") + ": " + m);
        lblA.setText(m.namaA());
        lblB.setText(m.namaB());
        cbSkorA.setSelectedItem(m.isSelesai() ? m.getSkorA() : 0);
        cbSkorB.setSelectedItem(m.isSelesai() ? m.getSkorB() : 0);
        lblFormat.setText(m instanceof GroupMatch
                ? "Fase grup dimainkan Bo2: skor yang sah 2-0, 1-1, atau 0-2 (seri boleh)."
                : "Babak gugur dimainkan Bo3: skor yang sah 2-0 atau 2-1 untuk pemenangnya (tidak boleh seri).");
        if (!m.siapDimainkan()) {
            lblFormat.setText("Lawan belum ditentukan. Skor bisa dicatat setelah babak sebelumnya selesai.");
        }
    }

    private void simpanSkor() {
        Match m = terpilih();
        if (m == null) {
            return;
        }
        int a = (Integer) cbSkorA.getSelectedItem();
        int b = (Integer) cbSkorB.getSelectedItem();
        boolean koreksi = m.isSelesai();
        try {
            getDipilih().catatHasil(m, a, b); // aturan skor dan urutan babak diperiksa di sini
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Input Skor", ex.getMessage());
            return;
        }
        Team pemenang = m.tentukanPemenang();
        frame.catatAktivitas((koreksi ? "Skor dikoreksi: " : "Skor dicatat: ") + m.namaA() + " " + a + " - " + b + " "
                + m.namaB() + (pemenang == null ? " (seri)" : ", pemenang " + pemenang.getNama()));
        if (getDipilih().isSelesai() && getDipilih().getJuara() != null) {
            Pesan.info(this, "Turnamen Selesai", "Juara " + getDipilih().getNama() + ": " + getDipilih().getJuara().getNama() + ".");
        }
        // Skor di tabel dan nama team di babak berikutnya ikut berubah
        daftarMatch = getDipilih().semuaMatch();
        isiTabel();
        aturForm(terpilih());
    }

    private void aturWaktu() {
        Match m = terpilih();
        if (m == null) {
            return;
        }
        LocalDateTime baru = new WaktuDialog(javax.swing.SwingUtilities.getWindowAncestor(this), m.toString(), m.getWaktu()).tampilkan();
        if (baru == null) {
            return;
        }
        try {
            m.aturJadwal(baru);
        } catch (AturanTurnamenException ex) {
            Pesan.peringatan(this, "Atur Waktu", ex.getMessage());
            return;
        }
        frame.catatAktivitas("Waktu " + m + " diubah ke " + Kerangka.WAKTU.format(baru));
        isiTabel();
    }
}
