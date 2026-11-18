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
