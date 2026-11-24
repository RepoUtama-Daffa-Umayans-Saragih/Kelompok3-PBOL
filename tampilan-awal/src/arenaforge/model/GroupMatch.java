package arenaforge.model;

/**
 * Pertandingan fase grup, format Bo2: skor 2-0, 1-1, atau 0-2.
 * Menang mendapat 3 poin, seri 1 poin, kalah 0. Boleh berakhir seri.
 */
public class GroupMatch extends Match {
    private final String grup;

    public GroupMatch(String id, String grup, Team a, Team b) {
        super(id, a, b, a.getNama(), b.getNama());
        this.grup = grup;
    }

    public String getGrup() {
        return grup;
    }

    @Override
    public String namaFase() {
        return "Fase Grup";
    }

    @Override
    protected void validasiSkor(int a, int b) throws AturanTurnamenException {
        if (a < 0 || b < 0 || a + b != 2) {
            throw new AturanTurnamenException("Fase grup dimainkan Bo2, jadi skornya harus 2-0, 1-1, atau 0-2.");
        }
    }

    /** Menang menurut skor; seri dan belum dimainkan menghasilkan null. */
    @Override
    public Team tentukanPemenang() {
        if (!isSelesai() || getSkorA().equals(getSkorB())) {
            return null;
        }
        return getSkorA() > getSkorB() ? getTeamA() : getTeamB();
    }

    /** Poin yang didapat sebuah team dari pertandingan ini: 3 menang, 1 seri, 0 kalah atau belum main. */
    public int hitungPoin(Team t) {
        if (!isSelesai() || !melibatkan(t)) {
            return 0;
        }
        return switch (hasilUntuk(t)) {
            case MENANG -> 3;
            case SERI -> 1;
            case KALAH -> 0;
        };
    }
}
