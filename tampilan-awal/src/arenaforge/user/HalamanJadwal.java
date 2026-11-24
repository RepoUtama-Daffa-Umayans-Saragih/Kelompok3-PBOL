package arenaforge.user;

import static arenaforge.user.PortalPlayer.INDONESIA;
import static arenaforge.user.PortalPlayer.bannerLaga;
import static arenaforge.user.PortalPlayer.jarak;
import static arenaforge.user.PortalPlayer.judulBagian;
import static arenaforge.user.PortalPlayer.kiri;
import static arenaforge.user.PortalPlayer.kosong;
import static arenaforge.user.PortalPlayer.namaFase;

import arenaforge.Tema;
import arenaforge.ui.Kartu;
import arenaforge.ui.PanelGulir;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Menu Jadwal: semua laga timmu yang belum dimainkan, dari yang paling dekat. */
class HalamanJadwal extends PanelGulir {
    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", INDONESIA);
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH.mm");

    /** Satu laga beserta lomba asalnya. */
    private record Agenda(Turnamen turnamen, Laga laga) {
    }

    HalamanJadwal(List<Turnamen> daftarTurnamen) {
        super(new BorderLayout());
        List<Agenda> agenda = new ArrayList<>();
        for (Turnamen t : daftarTurnamen) {
            for (Laga laga : t.lagaBelumMain()) {
                agenda.add(new Agenda(t, laga));
            }
        }
        agenda.sort(Comparator.comparing(a -> a.laga().waktu()));

        PanelGulir halaman = PortalPlayer.halaman("Jadwal terdekat",
                "Semua laga timmu yang belum dimainkan, urut dari yang paling dekat.");
        add(halaman, BorderLayout.CENTER);

        if (agenda.isEmpty()) {
            halaman.add(jarak(24));
            halaman.add(kosong("Belum ada laga terjadwal untuk timmu.",
                    "Jadwal muncul di sini setelah panitia menyusun pertandingan."));
            return;
        }

        Agenda pertama = agenda.get(0);
        halaman.add(jarak(24));
        halaman.add(bannerLaga(pertama.laga(), "Laga terdekat",
                pertama.turnamen().getNama() + ", " + namaFase(pertama.laga())));

        if (agenda.size() > 1) {
            halaman.add(jarak(28));
            halaman.add(judulBagian("Setelah itu"));
            LocalDate tanggalSebelumnya = null;
            for (Agenda a : agenda.subList(1, agenda.size())) {
                LocalDate tanggal = a.laga().waktu().toLocalDate();
                if (!tanggal.equals(tanggalSebelumnya)) {
                    halaman.add(jarak(14));
                    halaman.add(kiri(Tema.redup(TANGGAL.format(tanggal), Font.BOLD, 12f)));
                    halaman.add(jarak(6));
                    tanggalSebelumnya = tanggal;
                }
                halaman.add(barisAgenda(a));
                halaman.add(jarak(6));
            }
        }
    }

    private JPanel barisAgenda(Agenda a) {
        JLabel jam = Tema.label(JAM.format(a.laga().waktu()), Font.BOLD, 18f);
        jam.setPreferredSize(new Dimension(64, 24));

        JPanel isi = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        isi.setOpaque(false);
        isi.add(Tema.label(a.laga().timA() + " vs " + a.laga().timB(), Font.BOLD, 14f));
        isi.add(Tema.redup(a.turnamen().getNama() + " (" + a.turnamen().getGame() + "), " + namaFase(a.laga())));

        Kartu baris = new Kartu(new BorderLayout(16, 0)).warna(Tema.PERMUKAAN, null).lengkung(12);
        baris.setBorder(Tema.padding(12, 16, 12, 16));
        baris.add(jam, BorderLayout.WEST);
        baris.add(isi, BorderLayout.CENTER);
        baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, baris.getPreferredSize().height));
        return kiri(baris);
    }
}
