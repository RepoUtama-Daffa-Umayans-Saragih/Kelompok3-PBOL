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
    private final BorderPane akar = new BorderPane();
    private final Map<String, ToggleButton> menu = new LinkedHashMap<>();
    private String menuAktif;

    public PortalPlayer(String namaPlayer, List<Turnamen> daftarTurnamen) {
        this.namaPlayer = namaPlayer;
        // Turnamen dengan laga terdekat tampil paling depan
        this.daftarTurnamen = new ArrayList<>(daftarTurnamen);
        this.daftarTurnamen.sort(Comparator.comparing(PortalPlayer::waktuBerikutnya,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /** Membuat Scene JavaFX. Wajib dipanggil di JavaFX Application Thread. */
    public Scene buatScene() {
        URL css = PortalPlayer.class.getResource("portal.css");
        if (css == null) {
            throw new IllegalStateException("File tema portal.css tidak ditemukan");
        }
        Scene scene = new Scene(akar);
        scene.getStylesheets().add(css.toExternalForm());
        akar.setLeft(buatMenuSamping());
        bukaMenu(DASHBOARD);
        return scene;
    }

    private VBox buatMenuSamping() {
        ToggleGroup grup = new ToggleGroup();
        VBox samping = new VBox(4);
        for (String nama : List.of(DASHBOARD, JADWAL, IKUT_LOMBA, BUAT_LOMBA)) {
            ToggleButton tombol = new ToggleButton(nama);
            tombol.setToggleGroup(grup);
            tombol.setMaxWidth(Double.MAX_VALUE);
            tombol.getStyleClass().add("menu-samping");
            tombol.setOnAction(e -> bukaMenu(nama));
            menu.put(nama, tombol);
            samping.getChildren().add(tombol);
        }
        // Klik menu yang sedang aktif tidak boleh membuat semua menu tidak terpilih
        grup.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null && lama != null) {
                lama.setSelected(true);
            }
        });
        // Panah atas/bawah langsung membuka menu sebelah, jadi posisi fokus selalu
        // terlihat lewat blok marun menu yang terpilih
        samping.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            boolean atas = e.getCode() == KeyCode.UP;
            if ((atas || e.getCode() == KeyCode.DOWN) && e.getTarget() instanceof ToggleButton tombol
                    && menu.containsValue(tombol)) {
                List<String> urutan = new ArrayList<>(menu.keySet());
                int berikut = Math.max(0, Math.min(urutan.size() - 1,
                        urutan.indexOf(tombol.getText()) + (atas ? -1 : 1)));
                bukaMenu(urutan.get(berikut));
                menu.get(urutan.get(berikut)).requestFocus();
                e.consume();
            }
        });

        Region dorong = new Region();
        VBox.setVgrow(dorong, Priority.ALWAYS);
        Label catatan = label("Data contoh, belum diambil dari database.", "catatan");
        catatan.setWrapText(true);
        samping.getChildren().addAll(dorong, catatan);
        samping.getStyleClass().add("menu-kiri");
        return samping;
    }

    /** Menampilkan isi satu menu di sebelah kanan dan menandai menunya di samping. */
    void bukaMenu(String nama) {
        menu.get(nama).setSelected(true);
        if (nama.equals(menuAktif)) {
            return;
        }
        menuAktif = nama;
        Node isi = switch (nama) {
            case JADWAL -> new HalamanJadwal(daftarTurnamen).buat();
            case IKUT_LOMBA -> new HalamanIkutLomba().buat();
            case BUAT_LOMBA -> new HalamanBuatLomba().buat();
            default -> new HalamanDashboard(namaPlayer, daftarTurnamen, () -> bukaMenu(IKUT_LOMBA)).buat();
        };
        akar.setCenter(gulir(isi));
    }

    private static LocalDateTime waktuBerikutnya(Turnamen t) {
        Laga laga = t.lagaBerikutnya();
        return laga == null ? null : laga.waktu();
    }

    // ---- Komponen kecil yang dipakai semua halaman ----

    static Label label(String teks, String... kelas) {
        Label label = new Label(teks);
        label.getStyleClass().addAll(kelas);
        return label;
    }

    static <T extends Node> T beriJarakAtas(T node, double jarak) {
        VBox.setMargin(node, new Insets(jarak, 0, 0, 0));
        return node;
    }

    static ScrollPane gulir(Node isi) {
        ScrollPane gulir = new ScrollPane(isi);
        gulir.setFitToWidth(true);
        gulir.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return gulir;
    }

    /** Judul halaman dan satu kalimat penjelasan di bawahnya. */
    static VBox kepalaHalaman(String judul, String penjelasan) {
        Label lblPenjelasan = label(penjelasan, "teks-redup");
        lblPenjelasan.setWrapText(true);
        VBox halaman = new VBox(6, label(judul, "judul-halaman"), lblPenjelasan);
        halaman.getStyleClass().add("halaman");
        return halaman;
    }

    /** "Fase grup, Grup A" untuk laga grup, atau nama fase knockout seperti "Semifinal". */
    static String namaFase(Laga laga) {
        return laga.faseGrup() ? "Fase grup, " + laga.grup() : laga.fase();
    }

    /** Status tim sebagai label: lolos (merah tua), masih di grup (bingkai), atau tersingkir (abu). */
    static Label badgeStatus(String status) {
        String jenis = status.startsWith("Tersingkir") ? "badge-gugur"
                : status.startsWith("Fase grup") ? "badge-grup" : "badge-lolos";
        return label(status, "badge", jenis);
    }

    /** Blok laga yang paling penting di layar: siapa lawan dan kapan mainnya. */
    static VBox bannerLaga(Laga laga, String judul, String keterangan) {
        HBox tim = new HBox(14, label(laga.timA(), "banner-tim"), label("vs", "banner-vs"),
                label(laga.timB(), "banner-tim"));
        tim.setAlignment(Pos.BASELINE_LEFT);

        VBox banner = new VBox(6,
                label(judul + ": " + keterangan, "banner-label"),
                tim,
                label(WAKTU_PANJANG.format(laga.waktu()), "banner-waktu"));
        banner.getStyleClass().add("banner-laga");
        return banner;
    }

    /** Pesan untuk bagian yang belum ada isinya: apa sebabnya dan apa langkah berikutnya. */
    static VBox kosong(String judul, String saran) {
        Label lblSaran = label(saran, "teks-redup");
        lblSaran.setWrapText(true);
        VBox kosong = new VBox(4, label(judul, "kosong-judul"), lblSaran);
        kosong.getStyleClass().add("kosong");
        return kosong;
    }

    /**
     * Jalur turnamen dari fase grup sampai final. Tahap yang sudah dicapai menyala mengikuti
     * urutan palet (marun, merah tua, merah), jadi makin dekat ke final makin merah.
     */
    static HBox jalur(Turnamen t) {
        List<String> tahapan = t.tahapan();
        int sekarang = t.tahapSekarang();

        HBox jalur = new HBox(4);
        for (int i = 0; i < tahapan.size(); i++) {
            Region batang = new Region();
            batang.getStyleClass().addAll("batang-jalur",
                    i <= sekarang ? "nyala-" + tingkatNyala(i, tahapan.size()) : "padam");

            Label nama = label(tahapan.get(i).equals("Fase Grup") ? "Grup" : tahapan.get(i), "label-jalur");
            if (i == sekarang) {
                nama.getStyleClass().add("sekarang");
            } else if (i > sekarang) {
                nama.getStyleClass().add("belum");
            }

            VBox tahap = new VBox(6, batang, nama);
