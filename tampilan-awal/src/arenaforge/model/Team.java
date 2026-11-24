package arenaforge.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Team berisi roster pemain inti dan cadangan. Jumlahnya mengikuti game (mis. 5 inti + 2 cadangan).
 * Pemain pertama yang masuk menjadi inti sampai slot inti penuh, sisanya cadangan.
 */
public class Team {
    private final String id;
    private final String nama;
    private final Game game;
    private final List<Player> roster = new ArrayList<>();
    private int kunci; // > 0 selama team ikut turnamen yang jadwalnya sudah disusun

    public Team(String id, String nama, Game game) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama team wajib diisi.");
        }
        this.id = id;
        this.nama = nama.trim();
        this.game = game;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public Game getGame() {
        return game;
    }

    public List<Player> getRoster() {
        return Collections.unmodifiableList(roster);
    }

    public List<Player> getInti() {
        return roster.subList(0, Math.min(game.getInti(), roster.size()));
    }

    public List<Player> getCadangan() {
        return roster.size() <= game.getInti() ? List.of() : roster.subList(game.getInti(), roster.size());
    }

    public boolean isInti(Player p) {
        return roster.indexOf(p) >= 0 && roster.indexOf(p) < game.getInti();
    }

    /** Roster lengkap kalau semua slot inti terisi. Cadangan boleh kosong. */
    public boolean isLengkap() {
        return roster.size() >= game.getInti();
    }

    public boolean isTerkunci() {
        return kunci > 0;
    }

    void kunci() {
        kunci++;
    }

    void bukaKunci() {
        if (kunci > 0) {
            kunci--;
        }
    }

    public void tambahPlayer(Player p) throws AturanTurnamenException {
        if (isTerkunci()) {
            throw new AturanTurnamenException("Roster " + nama + " dikunci karena team ikut turnamen yang jadwalnya sudah disusun.");
        }
        if (p.getGame() != game) {
            throw new AturanTurnamenException(p.getNickname() + " bermain " + p.getGame().getNama()
                    + ", sedangkan " + nama + " adalah team " + game.getNama() + ".");
        }
        if (p.getTeam() != null) {
            throw new AturanTurnamenException(p.getNickname() + " sudah berada di team " + p.getTeam().getNama() + ".");
        }
        if (roster.size() >= game.kapasitas()) {
            throw new AturanTurnamenException("Roster " + nama + " sudah penuh (" + game.deskripsiRoster() + ").");
        }
        roster.add(p);
        p.tetapkanTeam(this);
    }

    public void hapusPlayer(Player p) throws AturanTurnamenException {
        if (isTerkunci()) {
            throw new AturanTurnamenException("Roster " + nama + " dikunci karena team ikut turnamen yang jadwalnya sudah disusun.");
        }
        if (!roster.remove(p)) {
            throw new AturanTurnamenException(p.getNickname() + " tidak ada di roster " + nama + ".");
        }
        p.tetapkanTeam(null);
    }

    /** Mengosongkan roster saat team dihapus. */
    public void bubarkan() throws AturanTurnamenException {
        if (isTerkunci()) {
            throw new AturanTurnamenException("Team " + nama + " sedang ikut turnamen yang jadwalnya sudah disusun.");
        }
        for (Player p : new ArrayList<>(roster)) {
            hapusPlayer(p);
        }
    }

    @Override
    public String toString() {
        return nama;
    }
}
