package arenaforge.user;

import static arenaforge.user.PortalPlayer.jalur;
import static arenaforge.user.PortalPlayer.jarak;
import static arenaforge.user.PortalPlayer.kiri;
import static arenaforge.user.PortalPlayer.kosong;
import static arenaforge.user.PortalPlayer.lencanaStatus;
import static arenaforge.user.PortalPlayer.namaFase;

import arenaforge.Tema;
import arenaforge.ui.GulirGelap;
import arenaforge.ui.Kartu;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * Menu Dashboard:
 * - Box tetap yang bisa di-scroll untuk menampilkan laga berikutnya dari player.
 * - Card turnamen per game dengan ukuran dipersempit dan posisi horizontal (dapat di-slide ke samping).
 * - Bagian laga berikutnya di dalam card telah dihilangkan.
 */
class HalamanDashboard extends PanelGulir {

    /** Satu agenda laga berikutnya beserta turnamen asalnya. */
    private record AgendaLaga(Turnamen turnamen, Laga laga) {
    }

    HalamanDashboard(String namaPlayer, List<Turnamen> daftarTurnamen, Runnable bukaIkutLomba) {
        super(new BorderLayout());
        PanelGulir halaman = PortalPlayer.halaman("Game dan lomba yang kamu ikuti",
                "Halo, " + namaPlayer + ". Ini semua game dan lomba yang sedang kamu ikuti.");
        add(halaman, BorderLayout.CENTER);

        if (daftarTurnamen.isEmpty()) {
            Tombol ikut = new Tombol("Masukkan kode lomba", Tombol.Jenis.KEDUA);
            ikut.addActionListener(e -> bukaIkutLomba.run());
            Kartu pesan = kosong("Kamu belum ikut lomba apa pun.",
                    "Minta kode lomba ke panitia, lalu masukkan di menu Ikut lomba.");
            pesan.add(ikut, BorderLayout.SOUTH);
            halaman.add(jarak(24));
            halaman.add(pesan);
            return;
        }

        // Kelompokkan lomba per game
        Map<String, List<Turnamen>> perGame = new LinkedHashMap<>();
        for (Turnamen t : daftarTurnamen) {
            perGame.computeIfAbsent(t.getGame(), game -> new ArrayList<>()).add(t);
        }

        // 1. Box tetap yang bisa di-scroll untuk laga berikutnya
        halaman.add(jarak(20));
        halaman.add(buatBoxLagaBerikutnya(daftarTurnamen));

        // 2. Daftar card horizontal yang dipersempit dan bisa di-slide ke samping
        halaman.add(jarak(28));
        halaman.add(buatDaftarKartuHorizontal(perGame));
    }

    /**
     * Membuat box dengan bentuk tetap untuk menampilkan laga berikutnya.
     * Bentuk box tetap, hanya isi di dalamnya yang bisa di-scroll.
     */
    private JPanel buatBoxLagaBerikutnya(List<Turnamen> daftarTurnamen) {
        List<AgendaLaga> agenda = new ArrayList<>();
        for (Turnamen t : daftarTurnamen) {
            for (Laga laga : t.lagaBelumMain()) {
                agenda.add(new AgendaLaga(t, laga));
            }
        }
        agenda.sort(Comparator.comparing(a -> a.laga().waktu()));

        JPanel isiBox = new JPanel();
        isiBox.setLayout(new BoxLayout(isiBox, BoxLayout.Y_AXIS));
        isiBox.setOpaque(false);

        if (agenda.isEmpty()) {
            JPanel kosong = new JPanel(new BorderLayout());
            kosong.setOpaque(false);
            kosong.setBorder(Tema.padding(32, 16, 32, 16));
            JLabel pesan = Tema.redup("Belum ada jadwal laga berikutnya untuk timmu.");
            pesan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            kosong.add(pesan, BorderLayout.CENTER);
            isiBox.add(kosong);
        } else {
            for (int i = 0; i < agenda.size(); i++) {
                AgendaLaga ag = agenda.get(i);
                isiBox.add(buatBarisLagaBox(ag.turnamen(), ag.laga(), i == agenda.size() - 1));
            }
        }

        JScrollPane gulir = GulirGelap.bungkus(isiBox);
        gulir.setOpaque(false);
        gulir.getViewport().setOpaque(false);
        gulir.setBorder(BorderFactory.createEmptyBorder());
        gulir.getVerticalScrollBar().setUnitIncrement(14);

        // Kartu pembungkus dengan ukuran dan bentuk tetap
        Kartu box = new Kartu(new BorderLayout()).warna(Tema.PERMUKAAN, Tema.GARIS).lengkung(16);
        box.setBorder(Tema.padding(4, 6, 4, 6));
        box.add(gulir, BorderLayout.CENTER);

        // Bentuk box tetap: tinggi 200px, lebar mengikuti container
        box.setPreferredSize(new Dimension(800, 200));
        box.setMinimumSize(new Dimension(300, 200));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel bagian = new JPanel();
        bagian.setLayout(new BoxLayout(bagian, BoxLayout.Y_AXIS));
        bagian.setOpaque(false);
        bagian.add(kiri(Tema.label("Laga Berikutnya", Font.BOLD, 17f)));
        bagian.add(jarak(4));
        bagian.add(kiri(Tema.redup("Jadwal pertandingan terdekat dari turnamen yang kamu ikuti.")));
        bagian.add(jarak(12));
        bagian.add(kiri(box));

        return kiri(bagian);
    }

    /**
     * Satu baris laga di dalam scroll box laga berikutnya (meniru tampilan video screen record).
     */
    private JPanel buatBarisLagaBox(Turnamen t, Laga laga, boolean barisTerakhir) {
        JPanel baris = new JPanel();
        baris.setLayout(new BoxLayout(baris, BoxLayout.Y_AXIS));
        baris.setOpaque(false);
        baris.setBorder(BorderFactory.createCompoundBorder(
                barisTerakhir ? BorderFactory.createEmptyBorder() : BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.GARIS),
                Tema.padding(12, 16, 12, 16)));

        // Waktu pertandingan dengan ikon jam custom
        JPanel barisWaktu = new JPanel();
        barisWaktu.setLayout(new BoxLayout(barisWaktu, BoxLayout.X_AXIS));
        barisWaktu.setOpaque(false);
        barisWaktu.add(ikonJam());
        barisWaktu.add(Box.createHorizontalStrut(8));

        String waktuTeks = PortalPlayer.WAKTU_PANJANG.format(laga.waktu());
        JLabel lblWaktu = Tema.label(waktuTeks, Font.BOLD, 12f);
        lblWaktu.setForeground(Tema.MERAH);
        barisWaktu.add(lblWaktu);

        // Pertandingan: Tim saya vs Lawan
        String lawan = laga.lawan(t.getTimSaya());
        JLabel lblLaga = Tema.label(t.getTimSaya() + "  vs  " + lawan, Font.BOLD, 15f);

        // Keterangan turnamen, game, dan fase
        JLabel lblDetail = Tema.redup(t.getNama() + " (" + t.getGame() + ") • " + namaFase(laga)
                + " • Tim: " + t.getTimSaya() + " (" + t.getRoleSaya() + ")");

        baris.add(kiri(barisWaktu));
        baris.add(jarak(4));
        baris.add(kiri(lblLaga));
        baris.add(jarak(4));
        baris.add(kiri(lblDetail));

        return kiri(baris);
    }

    /**
     * Ikon jam kecil anti-aliased untuk penanda waktu laga.
     */
    private static JComponent ikonJam() {
        return new JComponent() {
            {
                setPreferredSize(new Dimension(14, 14));
                setMaximumSize(new Dimension(14, 14));
                setMinimumSize(new Dimension(14, 14));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Tema.MERAH);
                g.drawOval(0, 0, 13, 13);
                g.drawLine(7, 7, 7, 3);
                g.drawLine(7, 7, 10, 7);
                g.dispose();
            }
        };
    }

    /**
     * Membuat deretan kartu game yang posisinya menyamping (horizontal)
     * dan dibungkus dalam scroll horizontal sehingga dapat di-slide.
     */
    private JPanel buatDaftarKartuHorizontal(Map<String, List<Turnamen>> perGame) {
        JPanel panelKartu = new JPanel();
        panelKartu.setLayout(new BoxLayout(panelKartu, BoxLayout.X_AXIS));
        panelKartu.setOpaque(false);

        perGame.forEach((game, daftar) -> {
            panelKartu.add(kartuGame(game, daftar));
            panelKartu.add(Box.createHorizontalStrut(16));
        });

        JScrollPane gulir = GulirGelap.bungkus(panelKartu);
        gulir.setOpaque(false);
        gulir.getViewport().setOpaque(false);
        gulir.setBorder(BorderFactory.createEmptyBorder());
        gulir.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gulir.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        gulir.getHorizontalScrollBar().setUnitIncrement(20);

        // Memungkinkan slide/scroll horizontal menggunakan scroll wheel mouse
        gulir.addMouseWheelListener(e -> {
            JScrollBar hbar = gulir.getHorizontalScrollBar();
            if (hbar != null && hbar.isVisible()) {
                hbar.setValue(hbar.getValue() + e.getUnitsToScroll() * 20);
            }
        });

        // Ukuran container horizontal: tinggi disesuaikan agar muat kartu dan scrollbar
        gulir.setPreferredSize(new Dimension(800, 255));
        gulir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 255));

        JPanel bagian = new JPanel();
        bagian.setLayout(new BoxLayout(bagian, BoxLayout.Y_AXIS));
        bagian.setOpaque(false);
        bagian.add(kiri(Tema.label("Turnamen & Game", Font.BOLD, 17f)));
        bagian.add(jarak(4));
        bagian.add(kiri(Tema.redup("Geser ke samping untuk melihat game dan lomba lainnya.")));
        bagian.add(jarak(12));
        bagian.add(kiri(gulir));

        return kiri(bagian);
    }

    /**
     * Kartu per game dengan ukuran dipersempit (lebar ~290px).
     */
    private Kartu kartuGame(String game, List<Turnamen> daftar) {
        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setOpaque(false);
        isi.add(kiri(Tema.label(game, Font.BOLD, 18f)));
        isi.add(jarak(4));
        isi.add(kiri(Tema.redup(daftar.size() + " lomba diikuti")));
        for (Turnamen t : daftar) {
            isi.add(jarak(12));
            isi.add(blokLomba(t));
        }

        Kartu kartu = new Kartu(new BorderLayout()).warna(Tema.PERMUKAAN, Tema.MARUN);
        kartu.setBorder(Tema.padding(16, 16, 16, 16));
        kartu.add(isi, BorderLayout.NORTH);

        // Ukuran card dipersempit: lebar 290px, tinggi tetap rapi
        Dimension ukuran = new Dimension(290, 220);
        kartu.setPreferredSize(ukuran);
        kartu.setMinimumSize(ukuran);
        kartu.setMaximumSize(ukuran);

        return kartu;
    }

    /**
     * Blok turnamen di dalam card:
     * Bagian "Laga berikutnya" di dalam card ini dihilangkan sesuai permintaan.
     */
    private JPanel blokLomba(Turnamen t) {
        JPanel blok = new JPanel();
        blok.setLayout(new BoxLayout(blok, BoxLayout.Y_AXIS));
        blok.setOpaque(false);
        blok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.GARIS), Tema.padding(10, 0, 0, 0)));
        blok.add(kiri(Tema.label(t.getNama(), Font.BOLD, 15f)));
        blok.add(jarak(4));
        blok.add(kiri(Tema.redup(t.getTimSaya() + " (" + t.getRoleSaya() + ")")));
        blok.add(jarak(8));
        blok.add(lencanaStatus(t.statusTim()));
        blok.add(jarak(10));
        blok.add(jalur(t));
        // Catatan: bagian laga berikutnya dihilangkan dari dalam card
        return kiri(blok);
    }
}
