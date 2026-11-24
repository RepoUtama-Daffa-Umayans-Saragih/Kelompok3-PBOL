package arenaforge.model;

import java.time.LocalDateTime;

/**
 * Pertandingan antara dua team. Class abstract: tidak bisa dibuat langsung, hanya lewat
 * GroupMatch (fase grup) atau KnockoutMatch (gugur). Cara menentukan pemenang berbeda di tiap turunan.
 * Slot team boleh masih kosong (null) sambil menunggu hasil babak sebelumnya, ditandai teks placeholder.
 */
public abstract class Match {
    private final String id;
    private Team teamA;
    private Team teamB;
    private String keteranganA;
    private String keteranganB;
    private LocalDateTime waktu;
    private Integer skorA;
    private Integer skorB;

    protected Match(String id, Team teamA, Team teamB, String keteranganA, String keteranganB) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.keteranganA = keteranganA;
        this.keteranganB = keteranganB;
    }

    /** Nama fase untuk ditampilkan, mis. "Fase Grup" atau "Semifinal". */
    public abstract String namaFase();

    /** Team pemenang, atau null kalau belum dimainkan atau berakhir seri. */
    public abstract Team tentukanPemenang();

    /** Memeriksa apakah skor ini sah menurut format pertandingan (Bo2 untuk grup, Bo3 untuk gugur). */
    protected abstract void validasiSkor(int a, int b) throws AturanTurnamenException;

    /** Dipanggil setelah hasil tersimpan. Knockout memakainya untuk memajukan pemenang. */
    protected void setelahHasilDicatat() {
    }

    /** Memeriksa apakah hasil yang sudah ada boleh diubah. Bawaan: boleh. */
    protected void periksaBolehDiubah() throws AturanTurnamenException {
    }

    public void aturJadwal(LocalDateTime waktuBaru) throws AturanTurnamenException {
        if (waktuBaru == null) {
            throw new AturanTurnamenException("Waktu pertandingan wajib diisi.");
        }
        this.waktu = waktuBaru;
    }

    /** Mencatat skor, menentukan pemenang, dan memperbarui statistik player. Skor lama dikoreksi bila ada. */
    public final void catatHasil(int a, int b) throws AturanTurnamenException {
        if (teamA == null || teamB == null) {
            throw new AturanTurnamenException("Lawan belum ditentukan, hasil belum bisa dicatat.");
        }
        validasiSkor(a, b);
        periksaBolehDiubah();
        batalkanStatistik();
        skorA = a;
        skorB = b;
        terapkanStatistik();
        setelahHasilDicatat();
    }

    private void terapkanStatistik() {
        for (Player p : teamA.getInti()) {
            p.updateStatistik(hasilUntuk(teamA));
        }
        for (Player p : teamB.getInti()) {
            p.updateStatistik(hasilUntuk(teamB));
        }
    }

    /** Menarik kembali statistik dari hasil yang tersimpan, dipakai saat koreksi atau saat turnamen dihapus. */
    public final void batalkanStatistik() {
        if (!isSelesai()) {
            return;
        }
        for (Player p : teamA.getInti()) {
            p.batalkanStatistik(hasilUntuk(teamA));
        }
        for (Player p : teamB.getInti()) {
            p.batalkanStatistik(hasilUntuk(teamB));
        }
    }

    /** Menghapus skor dan menarik statistiknya. */
    public final void hapusHasil() {
        batalkanStatistik();
        skorA = null;
        skorB = null;
    }

    public Hasil hasilUntuk(Team t) {
        Team pemenang = tentukanPemenang();
        if (pemenang == null) {
            return Hasil.SERI;
        }
        return pemenang == t ? Hasil.MENANG : Hasil.KALAH;
    }

    public boolean isSelesai() {
        return skorA != null && skorB != null;
    }

    public boolean melibatkan(Team t) {
        return t != null && (t == teamA || t == teamB);
    }

    /** Dua team sudah pasti, jadi hasil sudah bisa dicatat. */
    public boolean siapDimainkan() {
        return teamA != null && teamB != null;
    }

    /** Dipakai sub class untuk mengisi slot saat babak sebelumnya selesai. */
    protected void tetapkanTeam(boolean slotA, Team t) {
        if (slotA) {
            teamA = t;
        } else {
            teamB = t;
        }
    }

    public String getId() {
        return id;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    /** Nama team A, atau keterangan slot (mis. "Juara Grup A") kalau belum pasti. */
    public String namaA() {
        return teamA != null ? teamA.getNama() : keteranganA;
    }

    public String namaB() {
        return teamB != null ? teamB.getNama() : keteranganB;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public Integer getSkorA() {
        return skorA;
    }

    public Integer getSkorB() {
        return skorB;
    }

    /** "2 - 1", atau "-" kalau belum dimainkan. */
    public String teksSkor() {
        return isSelesai() ? skorA + " - " + skorB : "-";
    }

    @Override
    public String toString() {
        return namaA() + " vs " + namaB();
    }
}
