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
        }
    }

    /** Fase grup: menang 3 poin, seri 1 poin. */
    public int getPoin() {
        return menang * 3 + seri;
    }

    public int getSelisih() {
        return skorMasuk - skorKeluar;
    }

    public String getTim() {
        return tim;
    }

    public int getMain() {
        return main;
    }

    public int getMenang() {
        return menang;
    }

    public int getSeri() {
        return seri;
