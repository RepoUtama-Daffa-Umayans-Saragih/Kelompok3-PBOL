package arenaforge.user;

/** Satu baris klasemen grup. Angkanya dikumpulkan dari hasil laga lewat catat(). */
public class BarisKlasemen {
    private final String tim;
    private int main;
    private int menang;
    private int seri;
    private int kalah;
    private int skorMasuk;
    private int skorKeluar;

    public BarisKlasemen(String tim) {
        this.tim = tim;
    }

    /** Menambahkan hasil satu laga dari sisi tim ini. */
    void catat(int skorSendiri, int skorLawan) {
        main++;
        skorMasuk += skorSendiri;
        skorKeluar += skorLawan;
        if (skorSendiri > skorLawan) {
            menang++;
        } else if (skorSendiri == skorLawan) {
            seri++;
        } else {
            kalah++;
