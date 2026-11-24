package arenaforge.user;

import arenaforge.Tema;
import arenaforge.ui.JalurTurnamen;
import arenaforge.ui.Kartu;
import arenaforge.ui.Lencana;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Portal Player (Swing): menu samping di kiri (Dashboard, Jadwal, Ikut lomba, Buat lomba),
 * isi menu yang dipilih di kanan dan bisa digulir.
 */
public class PortalPlayer extends JPanel {
    static final String DASHBOARD = "Dashboard";
    static final String JADWAL = "Jadwal";
    static final String IKUT_LOMBA = "Ikut lomba";
    static final String BUAT_LOMBA = "Buat lomba";

    static final Locale INDONESIA = Locale.forLanguageTag("id-ID");
    // Penulisan jam gaya Indonesia: "pukul 17.00"
    static final DateTimeFormatter WAKTU_PANJANG = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy, 'pukul' HH.mm", INDONESIA);
    static final DateTimeFormatter WAKTU_PENDEK = DateTimeFormatter.ofPattern("EEE d MMM, HH.mm", INDONESIA);

    private final String namaPlayer;
    private final List<Turnamen> daftarTurnamen;
    private final Map<String, Tombol> menu = new LinkedHashMap<>();
    private final JScrollPane area = Tema.gulir(new JPanel());
    private String menuAktif;

    public PortalPlayer(String namaPlayer, List<Turnamen> daftarTurnamen) {
        super(new BorderLayout());
        this.namaPlayer = namaPlayer;
        // Turnamen dengan laga terdekat tampil paling depan
        this.daftarTurnamen = new ArrayList<>(daftarTurnamen);
        this.daftarTurnamen.sort(Comparator.comparing(PortalPlayer::waktuBerikutnya,
                Comparator.nullsLast(Comparator.naturalOrder())));

        add(buatMenuSamping(), BorderLayout.WEST);
        add(area, BorderLayout.CENTER);
        bukaMenu(DASHBOARD);
    }

    private JPanel buatMenuSamping() {
        JPanel samping = new JPanel();
        samping.setLayout(new BoxLayout(samping, BoxLayout.Y_AXIS));
        samping.setBackground(Tema.PERMUKAAN);
        samping.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.GARIS), Tema.padding(20, 12, 20, 12)));
        samping.setPreferredSize(new Dimension(220, 0));

        for (String nama : List.of(DASHBOARD, JADWAL, IKUT_LOMBA, BUAT_LOMBA)) {
            Tombol tombol = new Tombol(nama, Tombol.Jenis.MENU);
            tombol.setAlignmentX(Component.LEFT_ALIGNMENT);
            tombol.addActionListener(e -> bukaMenu(nama));
            menu.put(nama, tombol);
            samping.add(tombol);
            samping.add(Box.createVerticalStrut(4));
        }
        samping.add(Box.createVerticalGlue());
        TeksBungkus catatan = new TeksBungkus("Data contoh, belum diambil dari database.", 12f, Tema.TEKS_REDUP, 190);
        catatan.setAlignmentX(Component.LEFT_ALIGNMENT);
        samping.add(catatan);

        // Panah atas/bawah langsung membuka menu sebelah, jadi posisi fokus selalu terlihat
        pasangPanah(samping, KeyEvent.VK_UP, -1);
        pasangPanah(samping, KeyEvent.VK_DOWN, 1);
        return samping;
    }

    private void pasangPanah(JComponent samping, int tombolKeyboard, int arah) {
        String kunci = "geser" + arah;
        samping.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(tombolKeyboard, 0), kunci);
        samping.getActionMap().put(kunci, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> urutan = new ArrayList<>(menu.keySet());
                int berikut = Math.max(0, Math.min(urutan.size() - 1, urutan.indexOf(menuAktif) + arah));
                bukaMenu(urutan.get(berikut));
                menu.get(urutan.get(berikut)).requestFocusInWindow();
            }
        });
    }

    /** Menampilkan isi satu menu di sebelah kanan dan menandai menunya di samping. */
    void bukaMenu(String nama) {
        if (nama.equals(menuAktif)) {
            return;
        }
        menuAktif = nama;
        menu.forEach((judul, tombol) -> tombol.setAktif(judul.equals(nama)));
        JPanel isi = switch (nama) {
            case JADWAL -> new HalamanJadwal(daftarTurnamen);
            case IKUT_LOMBA -> new HalamanIkutLomba();
            case BUAT_LOMBA -> new HalamanBuatLomba();
            default -> new HalamanDashboard(namaPlayer, daftarTurnamen, () -> bukaMenu(IKUT_LOMBA));
        };
        area.setViewportView(isi);
        SwingUtilities.invokeLater(() -> area.getVerticalScrollBar().setValue(0));
    }

    private static LocalDateTime waktuBerikutnya(Turnamen t) {
        Laga laga = t.lagaBerikutnya();
        return laga == null ? null : laga.waktu();
    }

    // ---- Komponen kecil yang dipakai semua halaman ----

    /** Dasar halaman: kolom vertikal yang lebarnya mengikuti jendela, dengan judul dan penjelasan di atas. */
    static PanelGulir halaman(String judul, String penjelasan) {
        PanelGulir halaman = new PanelGulir(null);
        halaman.setLayout(new BoxLayout(halaman, BoxLayout.Y_AXIS));
        halaman.setBackground(Tema.HITAM);
        halaman.setBorder(Tema.padding(28, 32, 36, 32));
        halaman.add(kiri(Tema.kepalaHalaman(judul, penjelasan)));
        return halaman;
    }

    static <T extends JComponent> T kiri(T komponen) {
        komponen.setAlignmentX(Component.LEFT_ALIGNMENT);
        return komponen;
    }

    static Component jarak(int px) {
        return Box.createVerticalStrut(px);
    }

    static JLabel judulBagian(String teks) {
        return kiri(Tema.label(teks, Font.BOLD, 17f));
    }

    static JLabel labelKecil(String teks) {
        return kiri(Tema.redup(teks, Font.BOLD, 12f));
    }

    /** "Fase grup, Grup A" untuk laga grup, atau nama fase knockout seperti "Semifinal". */
    static String namaFase(Laga laga) {
        return laga.faseGrup() ? "Fase grup, " + laga.grup() : laga.fase();
    }

    /** Blok laga yang paling penting di layar: siapa lawan dan kapan mainnya. */
    static Kartu bannerLaga(Laga laga, String judul, String keterangan) {
        Kartu banner = new Kartu(new BorderLayout(0, 6)).warna(Tema.MARUN, null).lengkung(20);
        banner.setBorder(Tema.padding(20, 24, 20, 24));
        banner.add(Tema.label(judul + ": " + keterangan, Font.BOLD, 12f), BorderLayout.NORTH);
        banner.add(Tema.label(laga.timA() + "   vs   " + laga.timB(), Font.BOLD, 24f), BorderLayout.CENTER);
        banner.add(Tema.label(WAKTU_PANJANG.format(laga.waktu()), Font.PLAIN, 14f), BorderLayout.SOUTH);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, banner.getPreferredSize().height + 20));
        return kiri(banner);
    }

    /** Pesan untuk bagian yang belum ada isinya: apa sebabnya dan apa langkah berikutnya. */
    static Kartu kosong(String judul, String saran) {
        Kartu kosong = new Kartu(new BorderLayout(0, 4)).warna(Tema.PERMUKAAN, Tema.GARIS);
        kosong.add(Tema.label(judul, Font.BOLD, 14f), BorderLayout.NORTH);
        kosong.add(new TeksBungkus(saran, 13f, Tema.TEKS_REDUP, 560), BorderLayout.CENTER);
        return kiri(kosong);
    }

    /** Status tim sebagai lencana: lolos (merah tua), masih di grup (bingkai), atau tersingkir (abu). */
    static Lencana lencanaStatus(String status) {
        return kiri(Lencana.dariStatus(status));
    }

    static JalurTurnamen jalur(Turnamen t) {
        return kiri(new JalurTurnamen(t.tahapan(), t.tahapSekarang()));
    }
}
