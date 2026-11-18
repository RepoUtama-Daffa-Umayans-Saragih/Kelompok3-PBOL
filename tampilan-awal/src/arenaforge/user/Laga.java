package arenaforge.user;

import java.time.LocalDateTime;

/**
 * Satu pertandingan antara dua tim. Skor null berarti laga belum dimainkan.
 * Nama tim boleh berupa placeholder (mis. "Pemenang SF1") kalau lawannya belum ditentukan.
 *
 * @param grup nama grup untuk laga fase grup, null untuk laga knockout
 */
public record Laga(String fase, String grup, LocalDateTime waktu,
                   String timA, String timB, Integer skorA, Integer skorB) {

    public boolean selesai() {
        return skorA != null && skorB != null;
    }

    public boolean faseGrup() {
        return grup != null;
    }
