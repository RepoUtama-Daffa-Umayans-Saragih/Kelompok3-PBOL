package arenaforge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Jendela utama (JFrame) dengan BorderLayout:
 * NORTH = header, WEST = menu, CENTER = isi menu, SOUTH = status bar.
 */
public class MainFrame extends JFrame {
    private final String peran;
    private final JPanel konten = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();
    private final DashboardPanel dashboard = new DashboardPanel();

    public MainFrame(String peran) {
        super("ArenaForge - Game Tournament Manager");
        this.peran = peran;

        // Event: minta konfirmasi sebelum jendela ditutup
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                konfirmasiTutup();
            }
        });

        // BorderLayout adalah layout default JFrame
        add(buatHeader(), BorderLayout.NORTH);
        add(buatSidebar(), BorderLayout.WEST);
        add(konten, BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);

        tampilkanMenu("Dashboard", dashboard);
        catatAktivitas("Masuk sebagai " + peran);

        setSize(1000, 640);
        setMinimumSize(new Dimension(820, 520));
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
        JLabel lblPeran = new JLabel("Masuk sebagai: " + peran);
        lblPeran.setForeground(Color.WHITE);
        JButton btnKeluarAkun = new JButton("Keluar Akun");
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
        sidebar.setBackground(Tema.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.GARIS),
                Tema.padding(12, 12, 12, 12)));

        sidebar.add(judulBagian("MENU"));
        tambahMenu(sidebar, "Dashboard", false, () -> {
            dashboard.perbaruiRingkasan();
            return dashboard;
        });
        tambahMenu(sidebar, "Game & Role", true, () -> new PlaceholderPanel(
                "Kelola Game & Role", "Menambah game baru beserta jumlah pemain dan role-nya."));
        tambahMenu(sidebar, "Player", true, () -> new PlayerPanel(this));
        tambahMenu(sidebar, "Team & Roster", true, () -> new PlaceholderPanel(
                "Team & Roster", "Membuat team untuk satu game, lalu menambah atau mengeluarkan player."));
        tambahMenu(sidebar, "Turnamen", true, () -> new PlaceholderPanel(
                "Turnamen", "Mendaftarkan team ke turnamen dan menghapus peserta."));

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(judulBagian("PERTANDINGAN"));
        tambahMenu(sidebar, "Jadwal & Bracket", false, () -> new PlaceholderPanel(
                "Jadwal & Bracket", "Menyusun jadwal fase grup, lalu bagan knockout sampai final."));
        tambahMenu(sidebar, "Input Skor", true, () -> new PlaceholderPanel(
                "Input Skor", "Mencatat skor pertandingan dan menentukan pemenang."));
        tambahMenu(sidebar, "Klasemen", false, () -> new PlaceholderPanel(
                "Klasemen", "Leaderboard setiap grup berdasarkan poin dan selisih skor."));
        tambahMenu(sidebar, "Statistik Player", false, () -> new PlaceholderPanel(
                "Statistik Player", "Rekap performa player selama turnamen (menang dan kalah)."));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JLabel judulBagian(String teks) {
        JLabel label = Tema.label(teks, Font.BOLD, 11f);
        label.setForeground(Tema.TEKS_REDUP);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(Tema.padding(0, 2, 6, 0));
        return label;
    }

    /** Menambah satu tombol menu. Menu khusus panitia dinonaktifkan untuk penonton. */
    private void tambahMenu(JPanel sidebar, String nama, boolean khususPanitia, Supplier<JPanel> isi) {
        JButton tombol = new JButton(nama);
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setAlignmentX(Component.LEFT_ALIGNMENT);
        tombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, tombol.getPreferredSize().height + 8));

        if (khususPanitia && !isPanitia()) {
            tombol.setEnabled(false);
            tombol.setToolTipText("Khusus panitia");
        }
        tombol.addActionListener(e -> tampilkanMenu(nama, isi.get()));

        sidebar.add(tombol);
        sidebar.add(Box.createVerticalStrut(6));
    }

    /** Ganti isi bagian CENTER dengan panel menu yang dipilih. */
    private void tampilkanMenu(String nama, JPanel isi) {
        konten.removeAll();
        konten.add(isi, BorderLayout.CENTER);
        konten.revalidate();
        konten.repaint();
        lblStatus.setText("Menu: " + nama);
    }

    private JPanel buatStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.GARIS));
        status.add(lblStatus);
        return status;
    }

    private void keluarAkun() {
        int pilihan = JOptionPane.showConfirmDialog(this, "Keluar dari akun " + peran + "?",
                "Keluar Akun", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            dispose();
            Main.mulai();
        }
    }

    private void konfirmasiTutup() {
        int pilihan = JOptionPane.showConfirmDialog(this, "Tutup aplikasi ArenaForge?",
                "Tutup Aplikasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
