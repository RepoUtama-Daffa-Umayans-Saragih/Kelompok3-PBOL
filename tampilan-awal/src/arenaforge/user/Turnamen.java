package arenaforge.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Turnamen yang diikuti player: grup, semua laga, dan roster tim si player.
 * Klasemen dan status tim dihitung dari hasil laga, tidak diketik manual.
 */
public class Turnamen {
    /** Urutan fase knockout dari awal sampai akhir. */
    private static final List<String> URUTAN_KNOCKOUT = List.of("Perempat Final", "Semifinal", "Final");

    private final String nama;
    private final String game;
    private final String format;
    private final String timSaya;
    private final String roleSaya;
    private final int lolosPerGrup;
    private final Map<String, List<String>> grup = new LinkedHashMap<>();
    private final List<Laga> daftarLaga = new ArrayList<>();
