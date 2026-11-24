package arenaforge.user;

import static arenaforge.user.PortalPlayer.jarak;
import static arenaforge.user.PortalPlayer.kiri;
import static arenaforge.user.PortalPlayer.labelKecil;

import arenaforge.Tema;
import arenaforge.ui.Isian;
import arenaforge.ui.Kartu;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Menu Ikut lomba: player memasukkan kode dari panitia untuk menemukan lomba. */
class HalamanIkutLomba extends PanelGulir {
    private final JTextField tfKode = Isian.teks(18);
    private final JPanel wadahPesan = new JPanel(new BorderLayout());
    private final JPanel wadahHasil = new JPanel(new BorderLayout());

    HalamanIkutLomba() {
        super(new BorderLayout());
        PanelGulir halaman = PortalPlayer.halaman("Ikut lomba",
                "Masukkan kode lomba dari panitia. Kalau kodenya cocok, kamu bisa lanjut membuat tim untuk lomba itu.");
        add(halaman, BorderLayout.CENTER);

        JLabel lblKode = Tema.label("Kode lomba", Font.BOLD, 13f);
        lblKode.setLabelFor(tfKode);
        Tombol btnCek = new Tombol("Cek kode", Tombol.Jenis.UTAMA);
        // Event: tombol dan tombol Enter di kolom kode sama-sama memeriksa kode
        btnCek.addActionListener(e -> periksaKode());
        tfKode.addActionListener(e -> periksaKode());

        JPanel isian = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        isian.setOpaque(false);
        isian.add(tfKode);
        isian.add(javax.swing.Box.createHorizontalStrut(10));
        isian.add(btnCek);
        isian.setMaximumSize(new Dimension(Integer.MAX_VALUE, isian.getPreferredSize().height));

        wadahPesan.setOpaque(false);
        wadahHasil.setOpaque(false);

        halaman.add(jarak(24));
        halaman.add(kiri(lblKode));
        halaman.add(jarak(6));
        halaman.add(kiri(isian));
        halaman.add(jarak(6));
        halaman.add(kiri(wadahPesan));
        halaman.add(jarak(4));
        halaman.add(kiri(new TeksBungkus(
                "Kode demo untuk mencoba: LIGA-KAMPUS (masih buka) atau TKN-RANKED (kuota penuh).", 12f, Tema.TEKS_REDUP)));
        halaman.add(jarak(16));
        halaman.add(kiri(wadahHasil));
    }

    private void periksaKode() {
        wadahHasil.removeAll();
        String kode = tfKode.getText().trim();

        if (kode.isEmpty()) {
            tampilkanPesan("Kode lomba wajib diisi.", true);
            tfKode.requestFocusInWindow();
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
        wadahHasil.add(panelCocok(lomba), BorderLayout.NORTH);
        wadahHasil.revalidate();
        wadahHasil.repaint();
    }

    private Kartu panelCocok(Lomba lomba) {
        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setOpaque(false);
        isi.add(kiri(Tema.redup("Kode cocok", Font.BOLD, 12f)));
        isi.add(jarak(4));
        isi.add(kiri(Tema.label(lomba.nama(), Font.BOLD, 20f)));
        isi.add(jarak(4));
        isi.add(kiri(Tema.redup(lomba.game() + ", diselenggarakan " + lomba.penyelenggara())));
        isi.add(jarak(4));
        isi.add(kiri(Tema.label(lomba.terdaftar() + " dari " + lomba.kuota() + " peserta sudah terdaftar", Font.PLAIN, 13f)));
        isi.add(jarak(12));
        isi.add(kiri(new TeksBungkus("Langkah berikutnya: buat tim untuk lomba ini. Halaman buat tim sedang disiapkan.",
                13f, Tema.TEKS_REDUP, 500)));

        Kartu panel = new Kartu(new BorderLayout()).warna(Tema.PERMUKAAN, Tema.MARUN);
        panel.add(isi, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(560, Integer.MAX_VALUE));
        return panel;
    }

    /** Pesan di bawah kolom kode. galat = true untuk kesalahan, false untuk info biasa; null menyembunyikan. */
    private void tampilkanPesan(String teks, boolean galat) {
        wadahPesan.removeAll();
        if (teks != null) {
            wadahPesan.add(new TeksBungkus(teks, 13f, galat ? Tema.MERAH : Tema.TEKS), BorderLayout.CENTER);
        }
        wadahPesan.revalidate();
        wadahPesan.repaint();
        wadahHasil.revalidate();
        wadahHasil.repaint();
    }
}
