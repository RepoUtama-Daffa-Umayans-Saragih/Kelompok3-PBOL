package arenaforge.halaman;

import arenaforge.Tema;
import arenaforge.model.Leaderboard;
import arenaforge.model.Tournament;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.TabelData;
import arenaforge.ui.TeksBungkus;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Klasemen setiap grup. Dua team teratas (baris tebal) lolos ke babak gugur. Hanya-baca, terbuka untuk penonton. */
public class KlasemenPanel extends HalamanTurnamen {

    public KlasemenPanel() {
        super("Klasemen", "Peringkat setiap grup berdasarkan poin, lalu selisih skor.");
        mulai();
    }

    @Override
    protected void tampilkan(Tournament t, JPanel wadah) {
        if (!t.isJadwalDisusun()) {
            wadah.add(Kerangka.kosong("Klasemen " + t.getNama() + " belum ada.",
                    "Grup baru terbentuk setelah panitia menyusun jadwal di menu Turnamen."), BorderLayout.NORTH);
            return;
        }
        PanelGulir daftar = new PanelGulir(null);
        daftar.setLayout(new BoxLayout(daftar, BoxLayout.Y_AXIS));
        daftar.setBackground(Tema.HITAM);

        for (Leaderboard papan : t.getKlasemen().values()) {
            TabelData tabel = new TabelData("#", "Team", "Main", "M", "S", "K", "Selisih", "Poin")
                    .ratakanTengah(0, 2, 3, 4, 5, 6, 7).lebarKolom(0, 36).lebarKolom(1, 220);
            List<Leaderboard.Baris> urut = papan.tampilkanKlasemen();
            for (int i = 0; i < urut.size(); i++) {
                Leaderboard.Baris b = urut.get(i);
                String selisih = b.getSelisih() > 0 ? "+" + b.getSelisih() : String.valueOf(b.getSelisih());
                tabel.tambah(i + 1, b.getTeam().getNama(), b.getMain(), b.getMenang(), b.getSeri(), b.getKalah(),
                        selisih, b.getPoin());
            }
            tabel.setSorot(baris -> baris < 2);
            JComponent kartu = Kerangka.kartu(papan.getNamaGrup(), Kerangka.tabelPas(tabel));
            kartu.setAlignmentX(Component.LEFT_ALIGNMENT);
            kartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, kartu.getPreferredSize().height));
            daftar.add(kartu);
            daftar.add(Box.createVerticalStrut(14));
        }
        TeksBungkus catatan = new TeksBungkus("Baris tebal: dua team teratas tiap grup lolos ke babak gugur. "
                + "Menang 3 poin, seri 1 poin.", 12f, Tema.TEKS_REDUP, 640);
        catatan.setAlignmentX(Component.LEFT_ALIGNMENT);
        daftar.add(catatan);
        wadah.add(Tema.gulir(daftar), BorderLayout.CENTER);
    }
}
