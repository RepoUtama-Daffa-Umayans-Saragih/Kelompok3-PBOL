package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.MainFrame;
import arenaforge.Tema;
import arenaforge.model.Tournament;
import arenaforge.ui.Kartu;
import arenaforge.ui.Pesan;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Halaman pertama setelah login: ringkasan jumlah data dan catatan aktivitas. */
public class DashboardPanel extends JPanel {
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final MainFrame frame;
    private final JLabel lblGame = Tema.label("0", Font.BOLD, 30f);
    private final JLabel lblPlayer = Tema.label("0", Font.BOLD, 30f);
    private final JLabel lblTeam = Tema.label("0", Font.BOLD, 30f);
    private final JLabel lblTurnamen = Tema.label("0", Font.BOLD, 30f);
    private final JLabel ketTurnamen = Tema.redup("", Font.PLAIN, 12f);
    private final JTextArea taAktivitas = new JTextArea();

    public DashboardPanel(MainFrame frame) {
        super(new BorderLayout(0, 18));
        this.frame = frame;
        setBorder(Tema.padding(24, 28, 24, 28));

        Tombol btnContoh = null;
        if (frame.isPanitia()) {
            btnContoh = new Tombol("Muat Data Contoh", Tombol.Jenis.KEDUA);
            btnContoh.setToolTipText("Mengisi 8 team Mobile Legends beserta pemainnya dan satu turnamen untuk demo");
            btnContoh.addActionListener(e -> muatDataContoh());
        }
        add(Kerangka.kepala("Selamat datang di ArenaForge",
                "Kelola player, team, turnamen, jadwal, dan klasemen e-sports dalam satu aplikasi.", btnContoh),
                BorderLayout.NORTH);

        // GridLayout 1 x 4: kartu ringkasan berukuran sama besar
        JPanel kartu = new JPanel(new GridLayout(1, 4, 14, 0));
        kartu.setOpaque(false);
        kartu.add(buatKartu("GAME", lblGame, Tema.redup("dapat ditambah panitia", Font.PLAIN, 12f)));
        kartu.add(buatKartu("PLAYER", lblPlayer, Tema.redup("terdaftar", Font.PLAIN, 12f)));
        kartu.add(buatKartu("TEAM", lblTeam, Tema.redup("dengan roster", Font.PLAIN, 12f)));
        kartu.add(buatKartu("TURNAMEN", lblTurnamen, ketTurnamen));

        // JTextArea hanya-baca untuk log aktivitas
        taAktivitas.setEditable(false);
        taAktivitas.setLineWrap(true);
        taAktivitas.setWrapStyleWord(true);
        taAktivitas.setFont(Tema.font(Font.PLAIN, 13f));
        taAktivitas.setBorder(Tema.padding(8, 8, 8, 8));
        JScrollPane gulir = Tema.gulir(taAktivitas);
        gulir.getViewport().setBackground(Tema.PERMUKAAN);

        JPanel tengah = new JPanel(new BorderLayout(0, 18));
        tengah.setOpaque(false);
        tengah.add(kartu, BorderLayout.NORTH);
        tengah.add(Kerangka.kartu("Aktivitas Terbaru", gulir), BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);
        perbaruiRingkasan();
    }

    public void perbaruiRingkasan() {
        lblGame.setText(String.valueOf(DataSementara.GAME.getSize()));
        lblPlayer.setText(String.valueOf(DataSementara.PLAYER.getSize()));
        lblTeam.setText(String.valueOf(DataSementara.TEAM.getSize()));
        lblTurnamen.setText(String.valueOf(DataSementara.TURNAMEN.getSize()));
        int berjalan = 0;
        for (int i = 0; i < DataSementara.TURNAMEN.size(); i++) {
            Tournament t = DataSementara.TURNAMEN.get(i);
            if (t.isJadwalDisusun() && !t.isSelesai()) {
                berjalan++;
            }
        }
        ketTurnamen.setText(DataSementara.TURNAMEN.isEmpty() ? "belum ada turnamen" : berjalan + " sedang berjalan");
    }

    public void tambahAktivitas(String teks) {
        taAktivitas.append("[" + LocalTime.now().format(JAM) + "]  " + teks + "\n");
    }

    private void muatDataContoh() {
        if (!DataSementara.isiDataContoh()) {
            Pesan.info(this, "Data Contoh", "Data contoh hanya bisa dimuat kalau belum ada player, team, dan turnamen.");
            return;
        }
        perbaruiRingkasan();
        frame.catatAktivitas("Data contoh dimuat: 8 team Mobile Legends, 48 player, dan turnamen MLBB Cup 2026");
        Pesan.info(this, "Data Contoh Dimuat", "8 team dan turnamen MLBB Cup 2026 sudah siap. "
                + "Buka menu Turnamen untuk menyusun jadwalnya.");
    }

    private JPanel buatKartu(String judul, JLabel nilai, JLabel keterangan) {
        Kartu kartu = new Kartu(new BorderLayout(0, 4));
        kartu.setBorder(Tema.padding(14, 16, 14, 16));
        kartu.add(Tema.redup(judul, Font.BOLD, 11f), BorderLayout.NORTH);
        kartu.add(nilai, BorderLayout.CENTER);
        kartu.add(keterangan, BorderLayout.SOUTH);
        return kartu;
    }
}
