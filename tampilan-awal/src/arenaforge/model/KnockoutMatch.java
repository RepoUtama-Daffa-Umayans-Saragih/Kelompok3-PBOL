package arenaforge.model;

/**
 * Pertandingan gugur, format Bo3: pemenang harus menang 2 gim (2-0 atau 2-1) dan tidak boleh seri.
 * Pemenang maju ke slot di pertandingan berikutnya, yang kalah tersingkir.
 */
public class KnockoutMatch extends Match {
    private final String fase;
    private KnockoutMatch berikutnya;
    private boolean keSlotA;

    public KnockoutMatch(String id, String fase, Team a, Team b, String keteranganA, String keteranganB) {
        super(id, a, b, keteranganA, keteranganB);
        this.fase = fase;
    }

    /** Hubungkan ke pertandingan di babak berikutnya, beserta slot yang akan diisi pemenang. */
    public void hubungkan(KnockoutMatch berikutnya, boolean keSlotA) {
        this.berikutnya = berikutnya;
        this.keSlotA = keSlotA;
    }

    public KnockoutMatch getBerikutnya() {
        return berikutnya;
    }

    @Override
    public String namaFase() {
        return fase;
    }

    @Override
    protected void validasiSkor(int a, int b) throws AturanTurnamenException {
        boolean sah = Math.max(a, b) == 2 && Math.min(a, b) >= 0 && Math.min(a, b) <= 1;
        if (!sah) {
            throw new AturanTurnamenException("Babak gugur dimainkan Bo3: skornya harus 2-0, 2-1, 1-2, atau 0-2.");
        }
    }

    @Override
    protected void periksaBolehDiubah() throws AturanTurnamenException {
        if (berikutnya != null && berikutnya.isSelesai()) {
            throw new AturanTurnamenException("Hasil babak berikutnya sudah dicatat, jadi hasil ini tidak bisa diubah.");
        }
    }

    /** Pemenang selalu ada setelah dimainkan, karena Bo3 tidak mengenal seri. */
    @Override
    public Team tentukanPemenang() {
        if (!isSelesai()) {
            return null;
        }
        return getSkorA() > getSkorB() ? getTeamA() : getTeamB();
    }

    /** Memasukkan pemenang ke slot di pertandingan berikutnya. */
    public void majukanPemenang() {
        Team pemenang = tentukanPemenang();
        if (berikutnya != null && pemenang != null) {
            berikutnya.tetapkanTeam(keSlotA, pemenang);
        }
    }

    @Override
    protected void setelahHasilDicatat() {
        majukanPemenang();
    }

    /** Dipanggil Tournament untuk mengisi slot babak pertama dari klasemen grup. */
    void isiSlot(boolean slotA, Team t) {
        tetapkanTeam(slotA, t);
    }
}
