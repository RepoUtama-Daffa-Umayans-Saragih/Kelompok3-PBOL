package arenaforge.model;

import java.util.List;

/** Jenis game e-sports beserta ukuran roster dan daftar role-nya. */
public class Game {
    private final String nama;
    private final int inti;
    private final int cadangan;
    private final List<String> role;

    public Game(String nama, int inti, int cadangan, List<String> role) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama game wajib diisi.");
        }
        if (inti < 1 || inti > 10) {
            throw new IllegalArgumentException("Jumlah pemain inti harus 1 sampai 10.");
        }
        if (cadangan < 0 || cadangan > 5) {
            throw new IllegalArgumentException("Jumlah cadangan harus 0 sampai 5.");
        }
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Minimal satu role harus diisi.");
        }
        this.nama = nama.trim();
        this.inti = inti;
        this.cadangan = cadangan;
        this.role = List.copyOf(role);
    }

    public String getNama() {
        return nama;
    }

    public int getInti() {
        return inti;
    }

    public int getCadangan() {
        return cadangan;
    }

    public List<String> getRole() {
        return role;
    }

    public int kapasitas() {
        return inti + cadangan;
    }

    public boolean roleValid(String kandidat) {
        return role.contains(kandidat);
    }

    /** Mis. "5 inti + 2 cadangan", atau "1 pemain (solo)" untuk game perorangan. */
    public String deskripsiRoster() {
        if (inti == 1 && cadangan == 0) {
            return "1 pemain (solo)";
        }
        return inti + " inti" + (cadangan > 0 ? " + " + cadangan + " cadangan" : "");
    }

    /** Awalan kode lomba, mis. MLBB untuk Mobile Legends. */
    public String awalanKode() {
        return switch (nama) {
            case "Mobile Legends" -> "MLBB";
            case "Free Fire" -> "FF";
            case "Tekken" -> "TKN";
            default -> nama.replaceAll("[^A-Za-z0-9]", "").toUpperCase().substring(0, Math.min(4, nama.replaceAll("[^A-Za-z0-9]", "").length()));
        };
    }

    @Override
    public String toString() {
        return nama;
    }
}
