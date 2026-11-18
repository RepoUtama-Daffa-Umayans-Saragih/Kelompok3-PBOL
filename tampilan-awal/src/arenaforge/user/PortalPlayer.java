package arenaforge.user;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Portal Player (JavaFX) yang ditempel ke jendela Swing lewat JFXPanel.
 * Kiri: menu samping (Dashboard, Jadwal, Ikut lomba, Buat lomba). Kanan: isi menu yang dipilih.
 */
public class PortalPlayer {
    static final String DASHBOARD = "Dashboard";
    static final String JADWAL = "Jadwal";
    static final String IKUT_LOMBA = "Ikut lomba";
    static final String BUAT_LOMBA = "Buat lomba";

    static final Locale INDONESIA = Locale.forLanguageTag("id-ID");
    // Penulisan jam gaya Indonesia: "pukul 17.00"
    static final DateTimeFormatter WAKTU_PANJANG = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy, 'pukul' HH.mm", INDONESIA);
    static final DateTimeFormatter WAKTU_PENDEK = DateTimeFormatter.ofPattern("EEE d MMM, HH.mm", INDONESIA);

    private final String namaPlayer;
    private final List<Turnamen> daftarTurnamen;
