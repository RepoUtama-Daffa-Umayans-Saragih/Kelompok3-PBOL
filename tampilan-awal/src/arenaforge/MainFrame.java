package arenaforge;

import arenaforge.halaman.DashboardPanel;
import arenaforge.halaman.GamePanel;
import arenaforge.halaman.JadwalPanel;
import arenaforge.halaman.KlasemenPanel;
import arenaforge.halaman.PlayerPanel;
import arenaforge.halaman.SkorPanel;
import arenaforge.halaman.StatistikPanel;
import arenaforge.halaman.TeamPanel;
import arenaforge.halaman.TurnamenPanel;
import arenaforge.ui.Pesan;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Jendela utama untuk Panitia dan Penonton (JFrame) dengan BorderLayout:
 * NORTH = header, WEST = menu, CENTER = isi menu, SOUTH = status bar.
 * Penonton hanya bisa membuka menu yang bersifat lihat-saja.
 */
public class MainFrame extends JFrame {
    private final String peran;
    private final JPanel konten = new JPanel(new BorderLayout());
    private final JLabel lblStatus = Tema.redup("", Font.PLAIN, 12f);
    private final DashboardPanel dashboard;
    private final Map<String, Tombol> tombolMenu = new LinkedHashMap<>();

    public MainFrame(String peran) {
        super("ArenaForge - Game Tournament Manager");
        this.peran = peran;
        this.dashboard = new DashboardPanel(this);

        // Event: minta konfirmasi sebelum jendela ditutup
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                konfirmasiTutup();
            }
        });

        getContentPane().setBackground(Tema.HITAM);
        // BorderLayout adalah layout default JFrame
        add(buatHeader(), BorderLayout.NORTH);
        add(buatSidebar(), BorderLayout.WEST);
        add(konten, BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);

        tampilkanMenu("Dashboard", dashboard);
        catatAktivitas("Masuk sebagai " + peran);

        setSize(1180, 740);
        setMinimumSize(new Dimension(980, 600));
        setLocationRelativeTo(null);
    }

    public boolean isPanitia() {
        return "Panitia".equals(peran);
    }

    /** Catat aktivitas ke dashboard dan tampilkan juga di status bar. */
    public void catatAktivitas(String teks) {
        dashboard.tambahAktivitas(teks);
        lblStatus.setText(teks);
    }

    private JPanel buatHeader() {
        JLabel lblPeran = Tema.label("Masuk sebagai: " + peran, Font.PLAIN, 13f);
        Tombol btnKeluarAkun = new Tombol("Keluar Akun", Tombol.Jenis.KEDUA);
        btnKeluarAkun.addActionListener(e -> keluarAkun());

        // BoxLayout horizontal: label dan tombol sejajar di tengah secara vertikal
        JPanel kanan = new JPanel();
        kanan.setLayout(new BoxLayout(kanan, BoxLayout.X_AXIS));
        kanan.setOpaque(false);
        kanan.add(lblPeran);
        kanan.add(Box.createHorizontalStrut(12));
        kanan.add(btnKeluarAkun);

        JPanel header = Tema.header("ArenaForge", "Game Tournament Manager");
        header.add(kanan, BorderLayout.EAST);
        return header;
    }

    private JPanel buatSidebar() {
        // BoxLayout vertikal: tombol menu disusun dari atas ke bawah
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Tema.PERMUKAAN);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.GARIS), Tema.padding(16, 12, 12, 12)));

        sidebar.add(judulBagian("MENU"));
        tambahMenu(sidebar, "Dashboard", false, () -> {
            dashboard.perbaruiRingkasan();
            return dashboard;
        });
        tambahMenu(sidebar, "Game & Role", true, () -> new GamePanel(this));
        tambahMenu(sidebar, "Player", true, () -> new PlayerPanel(this));
        tambahMenu(sidebar, "Team & Roster", true, () -> new TeamPanel(this));
        tambahMenu(sidebar, "Turnamen", true, () -> new TurnamenPanel(this));

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(judulBagian("PERTANDINGAN"));
        tambahMenu(sidebar, "Jadwal & Bracket", false, JadwalPanel::new);
        tambahMenu(sidebar, "Input Skor", true, () -> new SkorPanel(this));
        tambahMenu(sidebar, "Klasemen", false, KlasemenPanel::new);
        tambahMenu(sidebar, "Statistik Player", false, StatistikPanel::new);

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JLabel judulBagian(String teks) {
        JLabel label = Tema.redup(teks, Font.BOLD, 11f);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(Tema.padding(0, 14, 6, 0));
        return label;
    }

    /** Menambah satu tombol menu. Menu khusus panitia dinonaktifkan untuk penonton. */
    private void tambahMenu(JPanel sidebar, String nama, boolean khususPanitia, Supplier<JPanel> isi) {
        Tombol tombol = new Tombol(nama, Tombol.Jenis.MENU);
        tombol.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (khususPanitia && !isPanitia()) {
            tombol.setEnabled(false);
            tombol.setToolTipText("Khusus panitia");
        }
        // Isi menu dibuat baru setiap diklik, jadi datanya selalu terbaru
        tombol.addActionListener(e -> tampilkanMenu(nama, isi.get()));

        tombolMenu.put(nama, tombol);
        sidebar.add(tombol);
        sidebar.add(Box.createVerticalStrut(4));
    }

    /** Ganti isi bagian CENTER dengan panel menu yang dipilih. */
    private void tampilkanMenu(String nama, JPanel isi) {
        konten.removeAll();
        konten.add(isi, BorderLayout.CENTER);
        konten.revalidate();
        konten.repaint();
        tombolMenu.forEach((judul, tombol) -> tombol.setAktif(judul.equals(nama)));
        lblStatus.setText("Menu: " + nama);
    }

    private JPanel buatStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        status.setBackground(Tema.PERMUKAAN);
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.GARIS));
        status.add(lblStatus);
        return status;
    }

    private void keluarAkun() {
        if (Pesan.konfirmasi(this, "Keluar Akun", "Keluar dari akun " + peran + "?")) {
            dispose();
            Main.mulai();
        }
    }

    private void konfirmasiTutup() {
        if (Pesan.konfirmasi(this, "Tutup Aplikasi", "Tutup aplikasi ArenaForge?")) {
            System.exit(0);
        }
    }
}
