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
    private final List<Anggota> roster = new ArrayList<>();

    public Turnamen(String nama, String game, String format, String timSaya, String roleSaya, int lolosPerGrup) {
        this.nama = nama;
        this.game = game;
        this.format = format;
        this.timSaya = timSaya;
        this.roleSaya = roleSaya;
        this.lolosPerGrup = lolosPerGrup;
    }

    public void tambahGrup(String namaGrup, List<String> tim) {
        grup.put(namaGrup, List.copyOf(tim));
    }

    public void tambahLaga(Laga laga) {
        daftarLaga.add(laga);
    }

    public void tambahAnggota(Anggota anggota) {
        roster.add(anggota);
    }

    public String getNama() {
        return nama;
    }

    public String getGame() {
        return game;
    }

    public String getFormat() {
        return format;
    }

    public String getTimSaya() {
        return timSaya;
    }

    public String getRoleSaya() {
        return roleSaya;
    }

    public int getLolosPerGrup() {
        return lolosPerGrup;
    }

    public List<String> getNamaGrup() {
        return new ArrayList<>(grup.keySet());
    }

    public List<Anggota> getRoster() {
        return Collections.unmodifiableList(roster);
    }

    /** False untuk placeholder seperti "Pemenang SF1" yang belum jadi tim sungguhan. */
    public boolean isTimTerdaftar(String tim) {
