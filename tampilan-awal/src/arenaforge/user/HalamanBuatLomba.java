package arenaforge.user;

import static arenaforge.user.PortalPlayer.beriJarakAtas;
import static arenaforge.user.PortalPlayer.kepalaHalaman;
import static arenaforge.user.PortalPlayer.label;

import arenaforge.DataSementara;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Menu Buat lomba: player membuat lomba sendiri dan mendapat kode untuk dibagikan. */
class HalamanBuatLomba {
    private final TextField tfNama = new TextField();
    private final ComboBox<String> cbGame = new ComboBox<>();
    private final ComboBox<Integer> cbKuota = new ComboBox<>();
    private final Label lblPesan = label("", "pesan");
    private final VBox daftarBuatan = new VBox(8);

    Node buat() {
        VBox halaman = kepalaHalaman("Buat lomba",
                "Buat lomba sendiri. Setelah dibuat, kamu dapat kode yang bisa dibagikan ke peserta "
                        + "lewat menu Ikut lomba.");

        tfNama.setPromptText("Contoh: Turnamen Antar Kelas");
        tfNama.setPrefColumnCount(24);
        tfNama.setOnAction(e -> buatLomba()); // Enter di kolom nama
        cbGame.getItems().setAll(DataSementara.GAME);
