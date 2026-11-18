package arenaforge.user;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Data contoh untuk Portal Player selama belum ada database.
 * Player demo: Viter (P01), ikut satu turnamen Mobile Legends dan satu Free Fire.
 */
public final class DataPortal {
    public static final String ID_PLAYER = "P01";
    public static final String NAMA_PLAYER = "Viter";

    /** Urutan laga setengah kompetisi untuk grup 4 tim: 6 laga dalam 3 matchday. */
    private static final int[][] PASANGAN_GRUP = {{0, 1}, {2, 3}, {0, 2}, {1, 3}, {0, 3}, {1, 2}};

    /** Huruf untuk kode lomba, tanpa 0/O dan 1/I supaya tidak tertukar saat diketik. */
    private static final String HURUF_KODE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ACAK = new SecureRandom();

    private static final List<Turnamen> DIIKUTI = List.of(mlbbCup(), ffShowdown());

    /** Semua lomba yang punya kode. Lomba dari menu Buat lomba ditambahkan ke sini. */
    private static final List<Lomba> LOMBA = new ArrayList<>(List.of(
            new Lomba("MLBB-CUP26", "MLBB Cup 2026", "Mobile Legends", 16, 16, "Panitia ArenaForge"),
            new Lomba("FF-SHOW26", "FF Showdown 2026", "Free Fire", 8, 8, "Panitia ArenaForge"),
            new Lomba("LIGA-KAMPUS", "Liga Kampus MLBB", "Mobile Legends", 16, 9, "Panitia ArenaForge"),
            new Lomba("TKN-RANKED", "Tekken Ranked Night", "Tekken", 8, 8, "Panitia ArenaForge")));

    private DataPortal() {
    }

    public static List<Turnamen> turnamenDiikuti() {
        return DIIKUTI;
    }

    /** Lomba dengan kode ini (huruf besar/kecil tidak dibedakan), atau null kalau tidak ada. */
