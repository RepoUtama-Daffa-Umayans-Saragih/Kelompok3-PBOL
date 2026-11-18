package arenaforge.user;

import static arenaforge.user.PortalPlayer.beriJarakAtas;
import static arenaforge.user.PortalPlayer.kepalaHalaman;
import static arenaforge.user.PortalPlayer.label;

import java.util.Locale;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Menu Ikut lomba: player memasukkan kode dari panitia untuk menemukan lomba. */
class HalamanIkutLomba {
    private final TextField tfKode = new TextField();
    private final Label lblPesan = label("", "pesan");
    private final VBox wadahHasil = new VBox();

    Node buat() {
        VBox halaman = kepalaHalaman("Ikut lomba",
                "Masukkan kode lomba dari panitia. Kalau kodenya cocok, kamu bisa lanjut membuat tim untuk lomba itu.");

        Label lblKode = label("Kode lomba", "label-isian");
        lblKode.setLabelFor(tfKode);
        tfKode.setPromptText("Contoh: LIGA-KAMPUS");
        tfKode.setPrefColumnCount(18);
        tfKode.setOnAction(e -> periksaKode()); // Enter di kolom kode

        Button btnCek = new Button("Cek kode");
        btnCek.getStyleClass().add("tombol-utama");
        btnCek.setOnAction(e -> periksaKode());

        HBox isian = new HBox(10, tfKode, btnCek);
        isian.setAlignment(Pos.CENTER_LEFT);

        lblPesan.setWrapText(true);
        lblPesan.setVisible(false);
