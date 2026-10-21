package arenaforge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Halaman pertama setelah login: ringkasan jumlah data dan catatan aktivitas. */
public class DashboardPanel extends JPanel {
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final JLabel lblJumlahPlayer = Tema.label("0", Font.BOLD, 28f);
    private final JTextArea taAktivitas = new JTextArea();

    public DashboardPanel() {
        super(new BorderLayout(0, 16));
        setBorder(Tema.padding(20, 20, 20, 20));

        JLabel lblKeterangan = Tema.label(
                "Kelola player, team, turnamen, jadwal, dan klasemen e-sports dalam satu aplikasi.",
                Font.PLAIN, 12f);
        lblKeterangan.setForeground(Tema.TEKS_REDUP);

        JPanel sambutan = new JPanel(new GridLayout(2, 1, 0, 4));
        sambutan.add(Tema.label("Selamat datang di ArenaForge", Font.BOLD, 20f));
        sambutan.add(lblKeterangan);

        // GridLayout 1 x 4: kartu ringkasan berukuran sama besar
        JPanel kartu = new JPanel(new GridLayout(1, 4, 12, 0));
        kartu.add(buatKartu("GAME", Tema.label(String.valueOf(DataSementara.GAME.length), Font.BOLD, 28f),
                "ML, Free Fire, Tekken"));
        kartu.add(buatKartu("PLAYER", lblJumlahPlayer, "terdaftar"));
        kartu.add(buatKartu("TEAM", Tema.label("0", Font.BOLD, 28f), "belum ada team"));
        kartu.add(buatKartu("TURNAMEN", Tema.label("0", Font.BOLD, 28f), "belum ada turnamen"));

        // JTextArea hanya-baca untuk log aktivitas
        taAktivitas.setEditable(false);
        taAktivitas.setLineWrap(true);
        taAktivitas.setWrapStyleWord(true);
        JScrollPane scrollAktivitas = new JScrollPane(taAktivitas);
        scrollAktivitas.setBorder(BorderFactory.createTitledBorder("Aktivitas Terbaru"));

        JPanel tengah = new JPanel(new BorderLayout(0, 16));
        tengah.add(kartu, BorderLayout.NORTH);
        tengah.add(scrollAktivitas, BorderLayout.CENTER);

        add(sambutan, BorderLayout.NORTH);
        add(tengah, BorderLayout.CENTER);
        perbaruiRingkasan();
    }

    public void perbaruiRingkasan() {
        lblJumlahPlayer.setText(String.valueOf(DataSementara.PLAYER.getSize()));
    }

    public void tambahAktivitas(String teks) {
        taAktivitas.append("[" + LocalTime.now().format(JAM) + "]  " + teks + "\n");
    }

    private JPanel buatKartu(String judul, JLabel nilai, String keterangan) {
        JLabel lblJudul = Tema.label(judul, Font.BOLD, 11f);
        lblJudul.setForeground(Tema.TEKS_REDUP);
        JLabel lblKeterangan = Tema.label(keterangan, Font.PLAIN, 11f);
        lblKeterangan.setForeground(Tema.TEKS_REDUP);

        JPanel kartu = new JPanel(new BorderLayout(0, 4));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 4, 1, 1, Tema.AKSEN),
                Tema.padding(12, 14, 12, 14)));
        kartu.add(lblJudul, BorderLayout.NORTH);
        kartu.add(nilai, BorderLayout.CENTER);
        kartu.add(lblKeterangan, BorderLayout.SOUTH);
        return kartu;
    }
}
