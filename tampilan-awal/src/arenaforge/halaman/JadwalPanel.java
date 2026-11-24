package arenaforge.halaman;

import arenaforge.Tema;
import arenaforge.model.GroupMatch;
import arenaforge.model.Leaderboard;
import arenaforge.model.Tournament;
import arenaforge.ui.BaganGugur;
import arenaforge.ui.PanelGulir;
import arenaforge.ui.TabelData;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Jadwal & Bracket: laga fase grup per grup, dan bagan gugur sampai final. Hanya-baca, terbuka untuk penonton. */
public class JadwalPanel extends HalamanTurnamen {

    public JadwalPanel() {
        super("Jadwal & Bracket", "Jadwal fase grup dan bagan gugur sampai final.");
        mulai();
    }

    @Override
    protected void tampilkan(Tournament t, JPanel wadah) {
        if (!t.isJadwalDisusun()) {
            wadah.add(Kerangka.kosong("Jadwal " + t.getNama() + " belum disusun.",
                    "Peserta terdaftar " + t.getPeserta().size() + " dari " + t.getKuota()
                            + ". Panitia menyusun jadwal di menu Turnamen setelah kuota penuh."), BorderLayout.NORTH);
            return;
        }

        CardLayout kartu = new CardLayout();
        JPanel isi = new JPanel(kartu);
        isi.setOpaque(false);
        isi.add(halamanGrup(t), "grup");
        JScrollPane gulirBagan = Tema.gulir(new BaganGugur(t));
        isi.add(gulirBagan, "bracket");

        Tombol btnGrup = new Tombol("Fase Grup", Tombol.Jenis.MENU);
        Tombol btnBagan = new Tombol("Bracket", Tombol.Jenis.MENU);
        btnGrup.setAktif(true);
        btnGrup.addActionListener(e -> {
            btnGrup.setAktif(true);
            btnBagan.setAktif(false);
            kartu.show(isi, "grup");
        });
        btnBagan.addActionListener(e -> {
            btnBagan.setAktif(true);
            btnGrup.setAktif(false);
            kartu.show(isi, "bracket");
        });
        JPanel tab = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tab.setOpaque(false);
        tab.add(btnGrup);
        tab.add(btnBagan);

        wadah.add(tab, BorderLayout.NORTH);
        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setOpaque(false);
        tengah.setBorder(Tema.padding(12, 0, 0, 0));
        tengah.add(isi, BorderLayout.CENTER);
        wadah.add(tengah, BorderLayout.CENTER);
    }

    private Component halamanGrup(Tournament t) {
        PanelGulir daftar = new PanelGulir(null);
        daftar.setLayout(new BoxLayout(daftar, BoxLayout.Y_AXIS));
        daftar.setBackground(Tema.HITAM);
        for (Leaderboard papan : t.getKlasemen().values()) {
            TabelData tabel = new TabelData("Pertandingan", "Waktu", "Skor", "Status").ratakanTengah(2).lebarKolom(0, 280).lebarKolom(1, 140).lebarKolom(2, 70).lebarKolom(3, 240);
            for (GroupMatch m : t.getPertandinganGrup()) {
                if (m.getGrup().equals(papan.getNamaGrup())) {
                    tabel.tambah(m.namaA() + " vs " + m.namaB(), Kerangka.WAKTU.format(m.getWaktu()), m.teksSkor(),
                            m.isSelesai() ? (m.tentukanPemenang() == null ? "Seri" : "Menang: " + m.tentukanPemenang().getNama())
                                    : "Belum dimainkan");
                }
            }
            javax.swing.JComponent kartu = Kerangka.kartu(papan.getNamaGrup(), Kerangka.tabelPas(tabel));
            kartu.setAlignmentX(Component.LEFT_ALIGNMENT);
            kartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, kartu.getPreferredSize().height));
            daftar.add(kartu);
            daftar.add(Box.createVerticalStrut(14));
        }
        return Tema.gulir(daftar);
    }
}
