package arenaforge.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Klasemen satu grup. Diurutkan dari poin, lalu selisih skor, lalu skor masuk. */
public class Leaderboard {

    /** Satu baris klasemen. */
    public static class Baris {
        private final Team team;
        private int main;
        private int menang;
        private int seri;
        private int kalah;
        private int skorMasuk;
        private int skorKeluar;

        Baris(Team team) {
            this.team = team;
        }

        void catat(int sendiri, int lawan) {
            main++;
            skorMasuk += sendiri;
            skorKeluar += lawan;
            if (sendiri > lawan) {
                menang++;
            } else if (sendiri == lawan) {
                seri++;
            } else {
                kalah++;
            }
        }

        public Team getTeam() {
            return team;
        }

        public int getMain() {
            return main;
        }

        public int getMenang() {
            return menang;
        }

        public int getSeri() {
            return seri;
        }

        public int getKalah() {
            return kalah;
        }

        public int getSkorMasuk() {
            return skorMasuk;
        }

        public int getSelisih() {
            return skorMasuk - skorKeluar;
        }

        /** Fase grup: menang 3 poin, seri 1 poin. */
        public int getPoin() {
            return menang * 3 + seri;
        }
    }

    private final String namaGrup;
    private final List<Team> anggota;
    private final Map<Team, Baris> baris = new LinkedHashMap<>();

    public Leaderboard(String namaGrup, List<Team> anggota) {
        this.namaGrup = namaGrup;
        this.anggota = List.copyOf(anggota);
        for (Team t : anggota) {
            baris.put(t, new Baris(t));
        }
    }

    public String getNamaGrup() {
        return namaGrup;
    }

    /** Menghitung ulang seluruh klasemen dari hasil pertandingan grup yang sudah selesai. */
    public void perbarui(List<GroupMatch> pertandingan) {
        baris.clear();
        for (Team t : anggota) {
            baris.put(t, new Baris(t));
        }
        for (GroupMatch m : pertandingan) {
            if (m.isSelesai()) {
                updatePoin(m.getTeamA(), m.getSkorA(), m.getSkorB());
                updatePoin(m.getTeamB(), m.getSkorB(), m.getSkorA());
            }
        }
    }

    /** Menambahkan hasil satu pertandingan dari sisi sebuah team. */
    public void updatePoin(Team t, int skorSendiri, int skorLawan) {
        baris.get(t).catat(skorSendiri, skorLawan);
    }

    public List<Baris> urutkanPeringkat() {
        List<Baris> hasil = new ArrayList<>(baris.values());
        hasil.sort(Comparator.comparingInt(Baris::getPoin)
                .thenComparingInt(Baris::getSelisih)
                .thenComparingInt(Baris::getSkorMasuk)
                .reversed());
        return hasil;
    }

    public List<Baris> tampilkanKlasemen() {
        return urutkanPeringkat();
    }
}
