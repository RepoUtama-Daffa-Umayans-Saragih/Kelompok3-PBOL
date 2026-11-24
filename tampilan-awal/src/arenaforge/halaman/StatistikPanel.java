package arenaforge.halaman;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.model.Player;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.TabelData;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

/** Statistik Player: rekap menang, seri, dan kalah dari semua pertandingan. Terbuka untuk penonton. */
public class StatistikPanel extends JPanel {
    private final TabelData tabel = new TabelData("Player", "Game", "Role", "Team", "Main", "Menang", "Seri", "Kalah", "Menang %")
            .ratakanTengah(4, 5, 6, 7, 8).lebarKolom(0, 170).lebarKolom(1, 140).lebarKolom(2, 110).lebarKolom(3, 150)
            .lebarKolom(4, 60).lebarKolom(5, 80).lebarKolom(6, 60).lebarKolom(7, 60).lebarKolom(8, 100);
    private final ComboGelap<String> cbGame;

    public StatistikPanel() {
        super(new BorderLayout(0, 16));
        setBorder(Tema.padding(24, 28, 24, 28));

        List<String> pilihan = new ArrayList<>();
        pilihan.add("Semua game");
        for (Game g : DataSementara.semuaGame()) {
            pilihan.add(g.getNama());
        }
        cbGame = new ComboGelap<>(pilihan.toArray(new String[0]));
        cbGame.addActionListener(e -> isiTabel());

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filter.setOpaque(false);
        filter.add(Tema.label("Game", Font.BOLD, 13f));
        filter.add(cbGame);

        add(Kerangka.kepala("Statistik Player", "Rekap performa player (pemain inti) selama turnamen, urut dari yang paling banyak menang.", filter),
                BorderLayout.NORTH);
        javax.swing.JScrollPane gulir = Tema.gulir(tabel);
        gulir.getViewport().setBackground(Tema.PERMUKAAN);
        add(gulir, BorderLayout.CENTER);
        isiTabel();
    }

    private void isiTabel() {
        String game = (String) cbGame.getSelectedItem();
        List<Player> daftar = new ArrayList<>();
        for (int i = 0; i < DataSementara.PLAYER.size(); i++) {
            Player p = DataSementara.PLAYER.get(i);
            if ("Semua game".equals(game) || p.getGame().getNama().equals(game)) {
                daftar.add(p);
            }
        }
        daftar.sort(Comparator.comparingInt(Player::getMenang).reversed()
                .thenComparing(Comparator.comparingInt(Player::persenMenang).reversed())
                .thenComparing(Player::getNickname));
        tabel.kosongkan();
        for (Player p : daftar) {
            tabel.tambah(p.getNickname() + " (" + p.getId() + ")", p.getGame().getNama(), p.getRole(),
                    p.getTeam() == null ? "-" : p.getTeam().getNama(), p.getMain(), p.getMenang(), p.getSeri(),
                    p.getKalah(), p.getMain() == 0 ? "-" : p.persenMenang() + "%");
        }
    }
}
