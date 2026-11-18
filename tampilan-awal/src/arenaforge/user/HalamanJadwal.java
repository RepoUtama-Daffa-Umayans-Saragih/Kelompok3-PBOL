package arenaforge.user;

import static arenaforge.user.PortalPlayer.INDONESIA;
import static arenaforge.user.PortalPlayer.bannerLaga;
import static arenaforge.user.PortalPlayer.beriJarakAtas;
import static arenaforge.user.PortalPlayer.kepalaHalaman;
import static arenaforge.user.PortalPlayer.kosong;
import static arenaforge.user.PortalPlayer.label;
import static arenaforge.user.PortalPlayer.namaFase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Menu Jadwal: semua laga timmu yang belum dimainkan, dari yang paling dekat. */
class HalamanJadwal {
    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", INDONESIA);
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH.mm");

    /** Satu laga beserta lomba asalnya. */
    private record Agenda(Turnamen turnamen, Laga laga) {
    }

    private final List<Turnamen> daftarTurnamen;

    HalamanJadwal(List<Turnamen> daftarTurnamen) {
        this.daftarTurnamen = daftarTurnamen;
    }

    Node buat() {
        List<Agenda> agenda = new ArrayList<>();
        for (Turnamen t : daftarTurnamen) {
            for (Laga laga : t.lagaBelumMain()) {
                agenda.add(new Agenda(t, laga));
            }
        }
