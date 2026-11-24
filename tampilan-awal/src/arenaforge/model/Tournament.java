package arenaforge.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Turnamen: menerima team, mengundi grup (4 team per grup), menyusun jadwal fase grup,
 * lalu bagan gugur dari perempat final sampai final. Dua tim teratas tiap grup lolos.
 * Kuota yang didukung: 4 (1 grup), 8 (2 grup), atau 16 (4 grup).
 */
public class Tournament {
    public static final int[] KUOTA_TERSEDIA = {4, 8, 16};
    private static final int TIM_PER_GRUP = 4;
    /** Pasangan indeks tim dalam grup: 6 laga dalam 3 matchday. */
    private static final int[][] PASANGAN = {{0, 1}, {2, 3}, {0, 2}, {1, 3}, {0, 3}, {1, 2}};

    private final String nama;
    private final Game game;
    private final int kuota;
    private final List<Team> peserta = new ArrayList<>();
    private final Map<String, List<Team>> grup = new LinkedHashMap<>();
    private final Map<String, Leaderboard> klasemen = new LinkedHashMap<>();
    private final List<GroupMatch> pertandinganGrup = new ArrayList<>();
    private final List<List<KnockoutMatch>> bagan = new ArrayList<>();
    private boolean jadwalDisusun;

    public Tournament(String nama, Game game, int kuota) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama turnamen wajib diisi.");
        }
        boolean sah = false;
        for (int k : KUOTA_TERSEDIA) {
            sah |= k == kuota;
        }
        if (!sah) {
            throw new IllegalArgumentException("Kuota turnamen harus 4, 8, atau 16 team.");
        }
        this.nama = nama.trim();
        this.game = game;
        this.kuota = kuota;
    }

    // ---- Pendaftaran peserta ----

    /** Menerima team kalau game sama, roster lengkap, belum terdaftar, dan kuota masih ada. */
    public void terimaTeam(Team t) throws AturanTurnamenException {
        if (jadwalDisusun) {
            throw new AturanTurnamenException("Jadwal sudah disusun, pendaftaran ditutup. Batalkan jadwal untuk mengubah peserta.");
        }
        if (t.getGame() != game) {
            throw new AturanTurnamenException(t.getNama() + " adalah team " + t.getGame().getNama()
                    + ", sedangkan turnamen ini untuk " + game.getNama() + ".");
        }
        if (peserta.contains(t)) {
            throw new AturanTurnamenException(t.getNama() + " sudah terdaftar di turnamen ini.");
        }
        if (peserta.size() >= kuota) {
            throw new AturanTurnamenException("Kuota " + kuota + " team sudah penuh.");
        }
        if (!t.isLengkap()) {
            throw new AturanTurnamenException("Roster " + t.getNama() + " belum lengkap: butuh " + game.getInti()
                    + " pemain inti, baru ada " + t.getRoster().size() + ".");
        }
        peserta.add(t);
    }

    public void hapusPeserta(Team t) throws AturanTurnamenException {
        if (jadwalDisusun) {
            throw new AturanTurnamenException("Jadwal sudah disusun. Batalkan jadwal dulu untuk mengeluarkan peserta.");
        }
        if (!peserta.remove(t)) {
            throw new AturanTurnamenException(t.getNama() + " bukan peserta turnamen ini.");
        }
    }

    // ---- Jadwal dan bagan ----

    /** Mengundi grup secara acak, lalu menyusun jadwal fase grup dan kerangka bagan gugur. */
    public void buatMatch() throws AturanTurnamenException {
        buatMatch(new Random(), LocalDate.now().plusDays(1));
    }

    public void buatMatch(Random acak, LocalDate mulai) throws AturanTurnamenException {
        if (jadwalDisusun) {
            throw new AturanTurnamenException("Jadwal turnamen ini sudah disusun.");
        }
        if (peserta.size() != kuota) {
            throw new AturanTurnamenException("Jadwal baru bisa disusun setelah kuota penuh: " + peserta.size()
                    + " dari " + kuota + " team terdaftar.");
        }
        List<Team> undian = new ArrayList<>(peserta);
        Collections.shuffle(undian, acak);

        int jumlahGrup = kuota / TIM_PER_GRUP;
        for (int g = 0; g < jumlahGrup; g++) {
            String namaGrup = "Grup " + (char) ('A' + g);
            List<Team> isi = new ArrayList<>(undian.subList(g * TIM_PER_GRUP, (g + 1) * TIM_PER_GRUP));
            grup.put(namaGrup, isi);
            for (int i = 0; i < PASANGAN.length; i++) {
                GroupMatch m = new GroupMatch(namaGrup.replace("Grup ", "G") + (i + 1), namaGrup,
                        isi.get(PASANGAN[i][0]), isi.get(PASANGAN[i][1]));
                int matchday = i / 2;
                m.aturJadwal(mulai.plusDays(matchday * 3L + g / 2).atTime(13 + (g % 2) * 4 + (i % 2) * 2, 0));
                pertandinganGrup.add(m);
            }
            Leaderboard papan = new Leaderboard(namaGrup, isi);
            klasemen.put(namaGrup, papan);
        }
        buatBagan(mulai.plusDays(3L * 2 + (jumlahGrup > 2 ? 1 : 0) + 2));
        jadwalDisusun = true;
        for (Team t : peserta) {
            t.kunci();
        }
        perbaruiKlasemen();
    }

    /** Kerangka bagan: babak pertama berisi placeholder "Juara Grup A" dst., babak lanjutan "Pemenang SF1". */
    private void buatBagan(LocalDate mulai) throws AturanTurnamenException {
        int lolos = kuota / 2;
        List<String> namaFase = switch (lolos) {
            case 2 -> List.of("Final");
            case 4 -> List.of("Semifinal", "Final");
            default -> List.of("Perempat Final", "Semifinal", "Final");
        };
        List<String> singkatan = switch (lolos) {
            case 2 -> List.of("F");
            case 4 -> List.of("SF", "F");
            default -> List.of("PF", "SF", "F");
        };

        int jumlah = lolos / 2;
        LocalDate tanggal = mulai;
        for (int r = 0; r < namaFase.size(); r++) {
            List<KnockoutMatch> babak = new ArrayList<>();
            for (int i = 0; i < jumlah; i++) {
                String ket = r == 0 ? null : "Pemenang " + singkatan.get(r - 1) + (2 * i + 1);
                String ketB = r == 0 ? null : "Pemenang " + singkatan.get(r - 1) + (2 * i + 2);
                String[] sumber = r == 0 ? keteranganBabakPertama(i) : new String[] {ket, ketB};
                KnockoutMatch m = new KnockoutMatch(singkatan.get(r) + (i + 1), namaFase.get(r), null, null, sumber[0], sumber[1]);
                m.aturJadwal(tanggal.plusDays(i / 2).atTime(i % 2 == 0 ? 13 : 16, 0));
                babak.add(m);
            }
            bagan.add(babak);
            tanggal = tanggal.plusDays(Math.max(1, (jumlah + 1) / 2) + 1);
            jumlah /= 2;
        }
        for (int r = 0; r + 1 < bagan.size(); r++) {
            for (int i = 0; i < bagan.get(r).size(); i++) {
                bagan.get(r).get(i).hubungkan(bagan.get(r + 1).get(i / 2), i % 2 == 0);
            }
        }
    }

    /**
     * Sumber slot babak pertama sebagai {grupA, peringkatA, grupB, peringkatB} (indeks mulai 0):
     * juara grup bertemu runner-up grup lain.
     */
    private int[] sumberBabakPertama(int i) {
        int jumlahGrup = kuota / TIM_PER_GRUP;
        return switch (jumlahGrup) {
            case 1 -> new int[] {0, 0, 0, 1};
            case 2 -> i == 0 ? new int[] {0, 0, 1, 1} : new int[] {1, 0, 0, 1};
            default -> switch (i) {
                case 0 -> new int[] {0, 0, 1, 1};
                case 1 -> new int[] {2, 0, 3, 1};
                case 2 -> new int[] {1, 0, 0, 1};
                default -> new int[] {3, 0, 2, 1};
            };
        };
    }

    private String[] keteranganBabakPertama(int i) {
        int[] s = sumberBabakPertama(i);
        return new String[] {keterangan(s[0], s[1]), keterangan(s[2], s[3])};
    }

    private static String keterangan(int grupKe, int peringkat) {
        return (peringkat == 0 ? "Juara Grup " : "Runner-up Grup ") + (char) ('A' + grupKe);
    }

    private void perbaruiKlasemen() {
        for (Map.Entry<String, Leaderboard> e : klasemen.entrySet()) {
            List<GroupMatch> laga = new ArrayList<>();
            for (GroupMatch m : pertandinganGrup) {
                if (m.getGrup().equals(e.getKey())) {
                    laga.add(m);
                }
            }
            e.getValue().perbarui(laga);
        }
    }

    /** Mengisi babak pertama gugur dari klasemen begitu semua laga grup selesai. */
    private void perbaruiBagan() {
        if (bagan.isEmpty() || adaHasilGugur()) {
            return;
        }
        boolean grupSelesai = pertandinganGrup.stream().allMatch(Match::isSelesai);
        List<String> namaGrup = new ArrayList<>(grup.keySet());
        for (int i = 0; i < bagan.get(0).size(); i++) {
            int[] s = sumberBabakPertama(i);
            KnockoutMatch m = bagan.get(0).get(i);
            m.isiSlot(true, grupSelesai ? klasemen.get(namaGrup.get(s[0])).urutkanPeringkat().get(s[1]).getTeam() : null);
            m.isiSlot(false, grupSelesai ? klasemen.get(namaGrup.get(s[2])).urutkanPeringkat().get(s[3]).getTeam() : null);
        }
    }

    /** Pintu masuk tunggal untuk mencatat skor: menerapkan aturan turnamen, lalu memperbarui klasemen dan bagan. */
    public void catatHasil(Match m, int a, int b) throws AturanTurnamenException {
        if (!jadwalDisusun) {
            throw new AturanTurnamenException("Jadwal belum disusun.");
        }
        if (m instanceof GroupMatch && adaHasilGugur()) {
            throw new AturanTurnamenException("Hasil fase grup tidak bisa diubah setelah babak gugur dimainkan.");
        }
        m.catatHasil(a, b);
        perbaruiKlasemen();
        perbaruiBagan();
    }

    /** Membatalkan jadwal supaya peserta bisa diubah lagi. Hanya boleh kalau belum ada skor tercatat. */
    public void batalkanJadwal() throws AturanTurnamenException {
        if (!jadwalDisusun) {
            throw new AturanTurnamenException("Jadwal belum disusun.");
        }
        if (adaHasil()) {
            throw new AturanTurnamenException("Sudah ada skor yang tercatat, jadwal tidak bisa dibatalkan.");
        }
        kosongkanJadwal();
    }

    /** Menghapus turnamen secara bersih: menarik statistik dari semua skor dan membuka kunci roster. */
    public void bubarkan() {
        for (Match m : semuaMatch()) {
            m.hapusHasil();
        }
        if (jadwalDisusun) {
            kosongkanJadwal();
        }
    }

    private void kosongkanJadwal() {
        for (Team t : peserta) {
            t.bukaKunci();
        }
        grup.clear();
        klasemen.clear();
        pertandinganGrup.clear();
        bagan.clear();
        jadwalDisusun = false;
    }

    // ---- Informasi ----

    public String getNama() {
        return nama;
    }

    public Game getGame() {
        return game;
    }

    public int getKuota() {
        return kuota;
    }

    public List<Team> getPeserta() {
        return Collections.unmodifiableList(peserta);
    }

    public boolean isJadwalDisusun() {
        return jadwalDisusun;
    }

    public Map<String, Leaderboard> getKlasemen() {
        return Collections.unmodifiableMap(klasemen);
    }

    public List<GroupMatch> getPertandinganGrup() {
        return Collections.unmodifiableList(pertandinganGrup);
    }

    public List<List<KnockoutMatch>> getBagan() {
        return Collections.unmodifiableList(bagan);
    }

    public List<Match> semuaMatch() {
        List<Match> semua = new ArrayList<>(pertandinganGrup);
        for (List<KnockoutMatch> babak : bagan) {
            semua.addAll(babak);
        }
        return semua;
    }

    public boolean adaHasil() {
        return semuaMatch().stream().anyMatch(Match::isSelesai);
    }

    private boolean adaHasilGugur() {
        return bagan.stream().flatMap(List::stream).anyMatch(Match::isSelesai);
    }

    public boolean isSelesai() {
        return !bagan.isEmpty() && bagan.get(bagan.size() - 1).get(0).isSelesai();
    }

    /** Juara turnamen, atau null kalau final belum dimainkan. */
    public Team getJuara() {
        return isSelesai() ? bagan.get(bagan.size() - 1).get(0).tentukanPemenang() : null;
    }

    public String statusTeks() {
        if (isSelesai()) {
            return "Selesai";
        }
        return jadwalDisusun ? "Berjalan" : "Pendaftaran";
    }

    @Override
    public String toString() {
        return nama;
    }
}
