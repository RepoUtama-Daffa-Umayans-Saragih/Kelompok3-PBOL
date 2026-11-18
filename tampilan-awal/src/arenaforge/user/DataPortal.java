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
    public static Lomba cariLomba(String kode) {
        String dicari = kode.trim().toUpperCase(Locale.ROOT);
        return LOMBA.stream().filter(lomba -> lomba.kode().equals(dicari)).findFirst().orElse(null);
    }

    public static boolean sudahDiikuti(Lomba lomba) {
        return DIIKUTI.stream().anyMatch(t -> t.getNama().equals(lomba.nama()));
    }

    public static boolean namaLombaDipakai(String nama) {
        return LOMBA.stream().anyMatch(lomba -> lomba.nama().equalsIgnoreCase(nama.trim()));
    }

    /** Lomba yang dibuat player sendiri, yang terbaru di depan. */
    public static List<Lomba> lombaBuatanSaya() {
        List<Lomba> hasil = new ArrayList<>();
        for (Lomba lomba : LOMBA) {
            if (lomba.penyelenggara().equals(NAMA_PLAYER)) {
                hasil.add(0, lomba);
            }
        }
        return hasil;
    }

    /** Membuat lomba baru milik player dengan kode unik, mis. "MLBB-7Q2K". */
    public static Lomba buatLomba(String nama, String game, int kuota) {
        String awalan = switch (game) {
            case "Mobile Legends" -> "MLBB";
            case "Free Fire" -> "FF";
            case "Tekken" -> "TKN";
            default -> "LOMBA";
        };
        String kode;
        do {
            StringBuilder akhiran = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                akhiran.append(HURUF_KODE.charAt(ACAK.nextInt(HURUF_KODE.length())));
            }
            kode = awalan + "-" + akhiran;
        } while (cariLomba(kode) != null);

        Lomba lomba = new Lomba(kode, nama.trim(), game, kuota, 0, NAMA_PLAYER);
        LOMBA.add(lomba);
        return lomba;
    }

    /** 16 tim, 4 grup. Fase grup dan perempat final selesai, semifinal belum dimainkan. */
    private static Turnamen mlbbCup() {
        Turnamen t = new Turnamen("MLBB Cup 2026", "Mobile Legends",
                "16 tim, fase grup Bo2, knockout Bo3", "Team Alpha", "Jungler", 2);

        isiGrup(t, "Grup A", LocalDate.of(2026, 11, 2), 3, 13,
                List.of("Team Alpha", "Rex Squad", "Nova Esports", "Iron Wolves"),
                "2-0", "1-1", "1-1", "2-0", "2-0", "2-0");
        isiGrup(t, "Grup B", LocalDate.of(2026, 11, 2), 3, 17,
                List.of("Team Omega", "Storm Raiders", "Blaze Unit", "Ember Knights"),
                "2-0", "0-2", "2-0", "2-0", "1-1", "2-0");
        isiGrup(t, "Grup C", LocalDate.of(2026, 11, 3), 3, 13,
                List.of("Team Beta", "Kraken Five", "Titan Core", "Rogue Unit"),
                "1-1", "2-0", "2-0", "2-0", "2-0", "1-1");
        isiGrup(t, "Grup D", LocalDate.of(2026, 11, 3), 3, 17,
                List.of("Vortex Gaming", "Shadow Lynx", "Crimson Owls", "Zenith Five"),
                "0-2", "2-0", "1-1", "2-0", "2-0", "1-1");

        // Juara grup bertemu runner-up grup lain
        laga(t, "Perempat Final", november(14, 13), "Team Alpha", "Storm Raiders", "2-1");
        laga(t, "Perempat Final", november(14, 16), "Team Beta", "Crimson Owls", "2-0");
        laga(t, "Perempat Final", november(15, 13), "Team Omega", "Rex Squad", "2-0");
        laga(t, "Perempat Final", november(15, 16), "Shadow Lynx", "Kraken Five", "1-2");
        laga(t, "Semifinal", november(21, 15), "Team Alpha", "Team Beta", null);
        laga(t, "Semifinal", november(21, 19), "Team Omega", "Kraken Five", null);
        laga(t, "Final", november(22, 19), "Pemenang SF1", "Pemenang SF2", null);
