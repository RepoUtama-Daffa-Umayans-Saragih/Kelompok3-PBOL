package arenaforge.user;

import static arenaforge.user.PortalPlayer.WAKTU_PENDEK;
import static arenaforge.user.PortalPlayer.jalur;
import static arenaforge.user.PortalPlayer.jarak;
import static arenaforge.user.PortalPlayer.kiri;
import static arenaforge.user.PortalPlayer.kosong;
import static arenaforge.user.PortalPlayer.labelKecil;
import static arenaforge.user.PortalPlayer.lencanaStatus;
import static arenaforge.user.PortalPlayer.namaFase;

import arenaforge.Tema;
import arenaforge.ui.Kartu;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/** Menu Dashboard: satu kartu per game, berisi lomba yang diikuti player di game itu. */
class HalamanDashboard extends PanelGulir {

    HalamanDashboard(String namaPlayer, List<Turnamen> daftarTurnamen, Runnable bukaIkutLomba) {
        super(new java.awt.BorderLayout());
        PanelGulir halaman = PortalPlayer.halaman("Game dan lomba yang kamu ikuti",
                "Halo, " + namaPlayer + ". Ini semua game dan lomba yang sedang kamu ikuti.");
        add(halaman, java.awt.BorderLayout.CENTER);

        if (daftarTurnamen.isEmpty()) {
            Tombol ikut = new Tombol("Masukkan kode lomba", Tombol.Jenis.KEDUA);
            ikut.addActionListener(e -> bukaIkutLomba.run());
            Kartu pesan = kosong("Kamu belum ikut lomba apa pun.",
                    "Minta kode lomba ke panitia, lalu masukkan di menu Ikut lomba.");
            pesan.add(ikut, java.awt.BorderLayout.SOUTH);
            halaman.add(jarak(24));
            halaman.add(pesan);
            return;
        }

        // Kelompokkan lomba per game; urutan mengikuti laga terdekat
        Map<String, List<Turnamen>> perGame = new LinkedHashMap<>();
        for (Turnamen t : daftarTurnamen) {
            perGame.computeIfAbsent(t.getGame(), game -> new ArrayList<>()).add(t);
        }

        // GridLayout 2 kolom: semua kartu sama tinggi, jadi barisnya rata
        JPanel kartu = new JPanel(new GridLayout(0, 2, 16, 16)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        kartu.setOpaque(false);
        perGame.forEach((game, daftar) -> kartu.add(kartuGame(game, daftar)));
        // Kalau jumlah game ganjil, sel terakhir diisi kosong supaya kartu tidak melebar penuh
        if (perGame.size() % 2 == 1) {
            JPanel kosongan = new JPanel();
            kosongan.setOpaque(false);
            kartu.add(kosongan);
        }
        halaman.add(jarak(24));
        halaman.add(kiri(kartu));
    }

    private Kartu kartuGame(String game, List<Turnamen> daftar) {
        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setOpaque(false);
        isi.add(kiri(Tema.label(game, Font.BOLD, 20f)));
        isi.add(jarak(4));
        isi.add(kiri(Tema.redup(daftar.size() + " lomba diikuti")));
        for (Turnamen t : daftar) {
            isi.add(jarak(14));
            isi.add(blokLomba(t));
        }
        Kartu kartu = new Kartu(new java.awt.BorderLayout()).warna(Tema.PERMUKAAN, Tema.MARUN);
        kartu.add(isi, java.awt.BorderLayout.NORTH);
        return kartu;
    }

    private JPanel blokLomba(Turnamen t) {
        Laga berikut = t.lagaBerikutnya();
        String teksBerikut = berikut == null
                ? "Belum ada jadwal laga berikutnya."
                : "vs " + berikut.lawan(t.getTimSaya()) + ", " + namaFase(berikut)
                        + "\n" + WAKTU_PENDEK.format(berikut.waktu());

        JPanel blok = new JPanel();
        blok.setLayout(new BoxLayout(blok, BoxLayout.Y_AXIS));
        blok.setOpaque(false);
        // Satu lomba dipisah garis tipis dari judul game
        blok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.GARIS), Tema.padding(14, 0, 0, 0)));
        blok.add(kiri(Tema.label(t.getNama(), Font.BOLD, 16f)));
        blok.add(jarak(4));
        blok.add(kiri(Tema.redup(t.getTimSaya() + " (" + t.getRoleSaya() + ")")));
        blok.add(jarak(8));
        blok.add(lencanaStatus(t.statusTim()));
        blok.add(jarak(14));
        blok.add(jalur(t));
        blok.add(jarak(14));
        blok.add(labelKecil("Laga berikutnya"));
        blok.add(jarak(4));
        blok.add(kiri(new TeksBungkus(teksBerikut, 13f, berikut == null ? Tema.TEKS_REDUP : Tema.TEKS, 400)));
        return kiri(blok);
    }
}
