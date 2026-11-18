package arenaforge.user;

import static arenaforge.user.PortalPlayer.WAKTU_PENDEK;
import static arenaforge.user.PortalPlayer.badgeStatus;
import static arenaforge.user.PortalPlayer.beriJarakAtas;
import static arenaforge.user.PortalPlayer.jalur;
import static arenaforge.user.PortalPlayer.kepalaHalaman;
import static arenaforge.user.PortalPlayer.kosong;
import static arenaforge.user.PortalPlayer.label;
import static arenaforge.user.PortalPlayer.namaFase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/** Menu Dashboard: satu kartu per game, berisi lomba yang diikuti player di game itu. */
class HalamanDashboard {
    private static final double LEBAR_KARTU = 384;

    private final String namaPlayer;
    private final List<Turnamen> daftarTurnamen;
    private final Runnable bukaIkutLomba;

    HalamanDashboard(String namaPlayer, List<Turnamen> daftarTurnamen, Runnable bukaIkutLomba) {
        this.namaPlayer = namaPlayer;
        this.daftarTurnamen = daftarTurnamen;
        this.bukaIkutLomba = bukaIkutLomba;
    }

    Node buat() {
        VBox halaman = kepalaHalaman("Game dan lomba yang kamu ikuti",
                "Halo, " + namaPlayer + ". Ini semua game dan lomba yang sedang kamu ikuti.");

        if (daftarTurnamen.isEmpty()) {
            Button ikut = new Button("Masukkan kode lomba");
            ikut.getStyleClass().add("tombol-kedua");
            ikut.setOnAction(e -> bukaIkutLomba.run());
            VBox pesan = kosong("Kamu belum ikut lomba apa pun.",
                    "Minta kode lomba ke panitia, lalu masukkan di menu Ikut lomba.");
            pesan.getChildren().add(beriJarakAtas(ikut, 12));
            halaman.getChildren().add(beriJarakAtas(pesan, 24));
            return halaman;
        }

        // Kelompokkan lomba per game; urutan mengikuti laga terdekat
        Map<String, List<Turnamen>> perGame = new LinkedHashMap<>();
        for (Turnamen t : daftarTurnamen) {
            perGame.computeIfAbsent(t.getGame(), game -> new ArrayList<>()).add(t);
        }

        // TilePane: semua kartu sama tinggi, jadi barisnya rata
        TilePane kartu = new TilePane(16, 16);
        kartu.setTileAlignment(Pos.TOP_LEFT);
        perGame.forEach((game, daftar) -> kartu.getChildren().add(kartuGame(game, daftar)));
        halaman.getChildren().add(beriJarakAtas(kartu, 24));
        return halaman;
    }

    private VBox kartuGame(String game, List<Turnamen> daftar) {
        VBox kartu = new VBox(4, label(game, "judul-kartu"), label(daftar.size() + " lomba diikuti", "teks-redup"));
        for (Turnamen t : daftar) {
            kartu.getChildren().add(beriJarakAtas(blokLomba(t), 14));
        }
