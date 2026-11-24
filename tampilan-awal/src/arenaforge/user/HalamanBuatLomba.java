package arenaforge.user;

import static arenaforge.user.PortalPlayer.jarak;
import static arenaforge.user.PortalPlayer.judulBagian;
import static arenaforge.user.PortalPlayer.kiri;

import arenaforge.DataSementara;
import arenaforge.Tema;
import arenaforge.model.Game;
import arenaforge.ui.ComboGelap;
import arenaforge.ui.Formulir;
import arenaforge.ui.Isian;
import arenaforge.ui.Kartu;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.TeksBungkus;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Menu Buat lomba: player membuat lomba sendiri dan mendapat kode untuk dibagikan. */
class HalamanBuatLomba extends PanelGulir {
    private final JTextField tfNama = Isian.teks(24);
    private final ComboGelap<Game> cbGame = new ComboGelap<>(DataSementara.semuaGame());
    private final ComboGelap<Integer> cbKuota = new ComboGelap<>(new Integer[] {4, 8, 16});
    private final JPanel wadahPesan = new JPanel(new BorderLayout());
    private final JPanel daftarBuatan = new JPanel();

    HalamanBuatLomba() {
        super(new BorderLayout());
        PanelGulir halaman = PortalPlayer.halaman("Buat lomba",
                "Buat lomba sendiri. Setelah dibuat, kamu dapat kode yang bisa dibagikan ke peserta "
                        + "lewat menu Ikut lomba.");
        add(halaman, BorderLayout.CENTER);

        cbKuota.setSelectedItem(8);
        tfNama.addActionListener(e -> buatLomba()); // Enter di kolom nama

        Formulir form = new Formulir()
                .tambah("Nama lomba", tfNama)
                .tambah("Game", cbGame)
                .tambah("Jumlah peserta", cbKuota);
        form.setMaximumSize(new Dimension(520, form.getPreferredSize().height));

        Tombol btnBuat = new Tombol("Buat lomba", Tombol.Jenis.UTAMA);
        btnBuat.addActionListener(e -> buatLomba());
        JPanel baristombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        baristombol.setOpaque(false);
        baristombol.add(btnBuat);
        baristombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, baristombol.getPreferredSize().height));

        wadahPesan.setOpaque(false);
        daftarBuatan.setLayout(new BoxLayout(daftarBuatan, BoxLayout.Y_AXIS));
        daftarBuatan.setOpaque(false);

        halaman.add(jarak(24));
        halaman.add(kiri(form));
        halaman.add(jarak(10));
        halaman.add(kiri(baristombol));
        halaman.add(jarak(8));
        halaman.add(kiri(wadahPesan));
        halaman.add(jarak(28));
        halaman.add(kiri(daftarBuatan));
        isiDaftarBuatan(null);
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
            tampilkanPesan(pesan, true);
            tfNama.requestFocusInWindow();
            return;
        }

        Game game = (Game) cbGame.getSelectedItem();
        Lomba baru = DataPortal.buatLomba(nama, game.getNama(), (Integer) cbKuota.getSelectedItem());
        tfNama.setText("");
        tampilkanPesan(baru.nama() + " sudah dibuat. Bagikan kodenya ke peserta.", false);
        isiDaftarBuatan(baru);
    }

    /** Daftar lomba buatan player beserta kodenya. Lomba yang baru dibuat ditandai. */
    private void isiDaftarBuatan(Lomba baru) {
        daftarBuatan.removeAll();
        List<Lomba> buatan = DataPortal.lombaBuatanSaya();
        if (!buatan.isEmpty()) {
            daftarBuatan.add(judulBagian("Lomba buatanmu"));
            for (Lomba lomba : buatan) {
                daftarBuatan.add(jarak(8));
                daftarBuatan.add(barisBuatan(lomba, lomba.equals(baru)));
            }
            daftarBuatan.add(jarak(12));
            daftarBuatan.add(kiri(new TeksBungkus(
                    "Menu admin untuk mengatur pertandingan lomba buatanmu sedang disiapkan.", 12f, Tema.TEKS_REDUP)));
        }
        daftarBuatan.revalidate();
        daftarBuatan.repaint();
    }

    private Kartu barisBuatan(Lomba lomba, boolean baruDibuat) {
        JPanel info = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        info.add(Tema.label(lomba.nama() + (baruDibuat ? " (baru dibuat)" : ""), Font.BOLD, 14f));
        info.add(Tema.redup(lomba.game() + ", " + lomba.terdaftar() + " dari " + lomba.kuota() + " peserta terdaftar"));

        javax.swing.JLabel kode = Tema.label(lomba.kode(), Font.BOLD, 18f);
        kode.setFont(Tema.fontKode(18f));
        Tombol btnSalin = new Tombol("Salin kode", Tombol.Jenis.KEDUA);
        btnSalin.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(lomba.kode()), null);
            btnSalin.setText("Kode disalin");
        });

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        kanan.setOpaque(false);
        kanan.add(kode);
        kanan.add(btnSalin);

        Kartu baris = new Kartu(new BorderLayout(16, 0)).warna(Tema.PERMUKAAN, baruDibuat ? Tema.MERAH_TUA : null).lengkung(12);
        baris.setBorder(Tema.padding(10, 16, 10, 8));
        baris.add(info, BorderLayout.CENTER);
        baris.add(kanan, BorderLayout.EAST);
        baris.setMaximumSize(new Dimension(760, baris.getPreferredSize().height));
        return kiri(baris);
    }

    private void tampilkanPesan(String teks, boolean galat) {
        wadahPesan.removeAll();
        wadahPesan.add(new TeksBungkus(teks, 13f, galat ? Tema.MERAH : Tema.TEKS), BorderLayout.CENTER);
        wadahPesan.revalidate();
        wadahPesan.repaint();
    }
}
