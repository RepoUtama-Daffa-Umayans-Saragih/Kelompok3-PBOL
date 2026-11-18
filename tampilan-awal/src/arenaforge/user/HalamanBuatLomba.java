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
        cbGame.getSelectionModel().selectFirst();
        cbKuota.getItems().setAll(4, 8, 16);
        cbKuota.getSelectionModel().select(Integer.valueOf(8));

        // GridPane 3 baris x 2 kolom: label di kiri, isian di kanan
        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(12);
        tambahIsian(form, 0, "Nama lomba", tfNama);
        tambahIsian(form, 1, "Game", cbGame);
        tambahIsian(form, 2, "Jumlah peserta", cbKuota);

        Button btnBuat = new Button("Buat lomba");
        btnBuat.getStyleClass().add("tombol-utama");
        btnBuat.setOnAction(e -> buatLomba());

        lblPesan.setWrapText(true);
        lblPesan.setVisible(false);
        lblPesan.setManaged(false);

        halaman.getChildren().addAll(beriJarakAtas(form, 24), beriJarakAtas(btnBuat, 8), lblPesan,
                beriJarakAtas(daftarBuatan, 28));
        isiDaftarBuatan(null);
        return halaman;
    }

    private static void tambahIsian(GridPane form, int baris, String judul, Region isian) {
        Label label = label(judul, "label-isian");
        label.setLabelFor(isian);
        form.add(label, 0, baris);
        form.add(isian, 1, baris);
    }

    private void buatLomba() {
        String nama = tfNama.getText().trim();
        String pesan = null;
        if (nama.isEmpty()) {
            pesan = "Nama lomba wajib diisi.";
        } else if (nama.length() < 3) {
            pesan = "Nama lomba minimal 3 huruf.";
        } else if (DataPortal.namaLombaDipakai(nama)) {
            pesan = "Nama \"" + nama + "\" sudah dipakai lomba lain. Pakai nama lain.";
        }
        if (pesan != null) {
