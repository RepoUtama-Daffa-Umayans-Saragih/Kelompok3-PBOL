package arenaforge.model;

/** Dilempar kalau suatu operasi melanggar aturan turnamen (roster penuh, kuota habis, skor tidak valid, dst.). */
public class AturanTurnamenException extends Exception {
    public AturanTurnamenException(String pesan) {
        super(pesan);
    }
}
