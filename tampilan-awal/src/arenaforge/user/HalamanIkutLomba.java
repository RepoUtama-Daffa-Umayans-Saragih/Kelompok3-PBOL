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
        lblPesan.setManaged(false);

        halaman.getChildren().addAll(
                beriJarakAtas(lblKode, 24),
                isian,
                lblPesan,
                label("Kode demo untuk mencoba: LIGA-KAMPUS (masih buka) atau TKN-RANKED (kuota penuh).", "catatan"),
                beriJarakAtas(wadahHasil, 16));
        return halaman;
    }

    private void periksaKode() {
        wadahHasil.getChildren().clear();
        String kode = tfKode.getText().trim();

        if (kode.isEmpty()) {
            tampilkanPesan("Kode lomba wajib diisi.", true);
            tfKode.requestFocus();
            return;
        }
        Lomba lomba = DataPortal.cariLomba(kode);
        if (lomba == null) {
            tampilkanPesan("Kode " + kode.toUpperCase(Locale.ROOT) + " tidak ditemukan. Periksa lagi huruf dan "
                    + "angkanya, atau minta kode baru ke panitia.", true);
            return;
        }
        if (DataPortal.sudahDiikuti(lomba)) {
            tampilkanPesan("Kamu sudah ikut " + lomba.nama() + ". Perkembangannya ada di menu Dashboard.", false);
            return;
        }
        if (lomba.penuh()) {
            tampilkanPesan("Kuota " + lomba.nama() + " sudah penuh (" + lomba.terdaftar() + " dari "
                    + lomba.kuota() + " peserta).", true);
            return;
        }

        tampilkanPesan(null, false);
        wadahHasil.getChildren().add(panelCocok(lomba));
    }

    private VBox panelCocok(Lomba lomba) {
        Label langkah = label("Langkah berikutnya: buat tim untuk lomba ini. Halaman buat tim sedang disiapkan.",
                "teks-redup");
        langkah.setWrapText(true);

        VBox panel = new VBox(4,
                label("Kode cocok", "label-kecil"),
                label(lomba.nama(), "judul-kartu"),
                label(lomba.game() + ", diselenggarakan " + lomba.penyelenggara(), "teks-redup"),
                label(lomba.terdaftar() + " dari " + lomba.kuota() + " peserta sudah terdaftar", "teks-isi"),
                beriJarakAtas(langkah, 12));
        panel.getStyleClass().add("panel-hasil");
        panel.setMaxWidth(560);
        return panel;
    }

    /** Pesan di bawah kolom kode. galat = true untuk kesalahan, false untuk info biasa; null menyembunyikan. */
    private void tampilkanPesan(String teks, boolean galat) {
        boolean ada = teks != null;
