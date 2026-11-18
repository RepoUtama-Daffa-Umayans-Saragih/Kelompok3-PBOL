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
        return grupDari(tim) != null;
    }

    public String grupDari(String tim) {
        for (Map.Entry<String, List<String>> entri : grup.entrySet()) {
            if (entri.getValue().contains(tim)) {
                return entri.getKey();
            }
        }
        return null;
    }

    public List<Laga> lagaGrup(String namaGrup) {
        return daftarLaga.stream()
                .filter(laga -> namaGrup.equals(laga.grup()))
                .sorted(Comparator.comparing(Laga::waktu))
                .toList();
    }

    /** Laga knockout satu fase, sesuai urutan di bagan (laga ke-1 dan ke-2 bertemu di fase berikutnya). */
    public List<Laga> lagaFase(String fase) {
        return daftarLaga.stream()
                .filter(laga -> !laga.faseGrup() && laga.fase().equals(fase))
                .toList();
    }

    /** Fase knockout yang dipakai turnamen ini, urut dari awal. */
    public List<String> faseKnockout() {
        return URUTAN_KNOCKOUT.stream()
                .filter(fase -> !lagaFase(fase).isEmpty())
                .toList();
    }

    /** Tahapan turnamen dari awal sampai akhir: fase grup, lalu fase knockout. */
    public List<String> tahapan() {
        List<String> daftar = new ArrayList<>();
        daftar.add("Fase Grup");
        daftar.addAll(faseKnockout());
        return daftar;
    }

    /** Indeks tahapan tempat tim si player berada sekarang (0 = fase grup). */
    public int tahapSekarang() {
        List<String> knockout = faseKnockout();
        int tahap = 0;
        for (int i = 0; i < knockout.size(); i++) {
            for (Laga laga : lagaFase(knockout.get(i))) {
                if (laga.melibatkan(timSaya)) {
                    tahap = i + 1;
                }
            }
        }
        return tahap;
    }

    /** Laga tim si player yang belum dimainkan, urut dari yang paling dekat. */
    public List<Laga> lagaBelumMain() {
        return daftarLaga.stream()
                .filter(laga -> !laga.selesai() && laga.melibatkan(timSaya))
                .sorted(Comparator.comparing(Laga::waktu))
                .toList();
    }

    /** Laga tim si player yang paling dekat dan belum dimainkan, atau null. */
    public Laga lagaBerikutnya() {
        List<Laga> belumMain = lagaBelumMain();
        return belumMain.isEmpty() ? null : belumMain.get(0);
    }

    /** Klasemen satu grup, diurutkan dari poin, lalu selisih skor, lalu skor masuk. */
    public List<BarisKlasemen> klasemen(String namaGrup) {
        Map<String, BarisKlasemen> baris = new LinkedHashMap<>();
        for (String tim : grup.get(namaGrup)) {
            baris.put(tim, new BarisKlasemen(tim));
        }
        for (Laga laga : lagaGrup(namaGrup)) {
            if (laga.selesai()) {
                baris.get(laga.timA()).catat(laga.skorA(), laga.skorB());
                baris.get(laga.timB()).catat(laga.skorB(), laga.skorA());
            }
        }

        List<BarisKlasemen> hasil = new ArrayList<>(baris.values());
        hasil.sort(Comparator.comparingInt(BarisKlasemen::getPoin)
                .thenComparingInt(BarisKlasemen::getSelisih)
                .thenComparingInt(BarisKlasemen::getSkorMasuk)
                .reversed());
        return hasil;
    }

    /** Peringkat tim di grupnya, mulai dari 1. Hasilnya -1 kalau tim tidak ada di grup mana pun. */
    public int peringkat(String tim) {
        String namaGrup = grupDari(tim);
        if (namaGrup == null) {
            return -1;
        }
        List<BarisKlasemen> daftar = klasemen(namaGrup);
        for (int i = 0; i < daftar.size(); i++) {
            if (daftar.get(i).getTim().equals(tim)) {
                return i + 1;
            }
        }
        return -1;
    }

    /** Baris klasemen tim, atau null kalau tim tidak ada di grup mana pun. */
    public BarisKlasemen barisKlasemen(String tim) {
        int posisi = peringkat(tim);
        return posisi < 1 ? null : klasemen(grupDari(tim)).get(posisi - 1);
    }

    /** Status tim si player, mis. "Lolos ke Semifinal" atau "Fase grup, peringkat 1 di Grup B". */
    public String statusTim() {
        Laga knockoutTerakhir = null;
        for (String fase : faseKnockout()) {
            for (Laga laga : lagaFase(fase)) {
                if (laga.melibatkan(timSaya)) {
                    knockoutTerakhir = laga;
                }
            }
        }

        if (knockoutTerakhir != null) {
            if (!knockoutTerakhir.selesai()) {
                return "Lolos ke " + knockoutTerakhir.fase();
            }
            if (timSaya.equals(knockoutTerakhir.pemenang())) {
                return knockoutTerakhir.fase().equals("Final") ? "Juara" : "Menang di " + knockoutTerakhir.fase();
            }
            return "Tersingkir di " + knockoutTerakhir.fase();
        }

        String namaGrup = grupDari(timSaya);
        if (namaGrup == null) {
            return "Menunggu jadwal";
        }
        if (barisKlasemen(timSaya).getMain() == 0) {
            return "Fase grup, belum main di " + namaGrup; // peringkat tanpa laga belum berarti apa-apa
        }
        int posisi = peringkat(timSaya);
        List<Laga> lagaDiGrup = lagaGrup(namaGrup);
        boolean grupSelesai = !lagaDiGrup.isEmpty() && lagaDiGrup.stream().allMatch(Laga::selesai);
        if (grupSelesai && posisi > lolosPerGrup) {
            return "Tersingkir di fase grup";
        }
        return "Fase grup, peringkat " + posisi + " di " + namaGrup;
    }
