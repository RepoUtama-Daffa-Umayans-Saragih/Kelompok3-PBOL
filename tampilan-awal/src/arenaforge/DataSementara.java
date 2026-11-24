package arenaforge;

import arenaforge.model.AturanTurnamenException;
import arenaforge.model.Game;
import arenaforge.model.Player;
import arenaforge.model.Team;
import arenaforge.model.Tournament;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Data yang disimpan di memori selama aplikasi berjalan.
 * Nanti diganti dengan database MySQL lewat JDBC.
 */
public final class DataSementara {
    /** Game yang tersedia. Panitia bisa menambah game baru lewat menu Game & Role. */
    public static final DefaultListModel<Game> GAME = new DefaultListModel<>();
    public static final DefaultListModel<Player> PLAYER = new DefaultListModel<>();
    public static final DefaultListModel<Team> TEAM = new DefaultListModel<>();
    public static final DefaultListModel<Tournament> TURNAMEN = new DefaultListModel<>();

    private static final String[] NICKNAME_CONTOH = {
        "Viter", "Raka", "Dimas", "Sasa", "Bima", "Fajar", "Arga", "Citra", "Dewa", "Eko", "Farah", "Galih",
        "Hana", "Indra", "Jaya", "Kirana", "Laras", "Mahesa", "Nando", "Olla", "Panji", "Qori", "Rafi", "Salsa",
        "Tegar", "Ulfa", "Vino", "Wawan", "Xena", "Yudha", "Zaki", "Aldi", "Bella", "Candra", "Dika", "Elang",
        "Fikri", "Gita", "Hafiz", "Ilham", "Joko", "Kenzo", "Lintang", "Mira", "Naufal", "Oscar", "Pandu", "Rizky"};
    private static final String[] TEAM_CONTOH = {
        "Team Alpha", "Rex Squad", "Nova Esports", "Iron Wolves", "Team Omega", "Storm Raiders", "Blaze Unit", "Ember Knights"};

    static {
        GAME.addElement(new Game("Mobile Legends", 5, 2, List.of("Jungler", "Roamer", "Mid Laner", "Gold Laner", "EXP Laner")));
        GAME.addElement(new Game("Free Fire", 4, 2, List.of("Rusher", "Sniper", "Support", "IGL")));
        GAME.addElement(new Game("Tekken", 1, 0, List.of("Solo")));
    }

    private DataSementara() {
    }

    public static Game cariGame(String nama) {
        for (int i = 0; i < GAME.size(); i++) {
            if (GAME.get(i).getNama().equalsIgnoreCase(nama.trim())) {
                return GAME.get(i);
            }
        }
        return null;
    }

    public static Game[] semuaGame() {
        Game[] hasil = new Game[GAME.size()];
        GAME.copyInto(hasil);
        return hasil;
    }

    public static boolean idSudahDipakai(String id) {
        for (int i = 0; i < PLAYER.size(); i++) {
            if (PLAYER.get(i).getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean namaTeamDipakai(String nama) {
        for (int i = 0; i < TEAM.size(); i++) {
            if (TEAM.get(i).getNama().equalsIgnoreCase(nama.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean namaTurnamenDipakai(String nama) {
        for (int i = 0; i < TURNAMEN.size(); i++) {
            if (TURNAMEN.get(i).getNama().equalsIgnoreCase(nama.trim())) {
                return true;
            }
        }
        return false;
    }

    /** ID team berikutnya yang belum dipakai: T01, T02, dst. */
    public static String idTeamBerikut() {
        int nomor = TEAM.size() + 1;
        while (true) {
            String id = String.format("T%02d", nomor);
            boolean dipakai = false;
            for (int i = 0; i < TEAM.size(); i++) {
                dipakai |= TEAM.get(i).getId().equals(id);
            }
            if (!dipakai) {
                return id;
            }
            nomor++;
        }
    }

    /** Game ini masih dipakai player, team, atau turnamen, jadi tidak boleh dihapus. */
    public static boolean gameDipakai(Game game) {
        for (int i = 0; i < PLAYER.size(); i++) {
            if (PLAYER.get(i).getGame() == game) {
                return true;
            }
        }
        for (int i = 0; i < TEAM.size(); i++) {
            if (TEAM.get(i).getGame() == game) {
                return true;
            }
        }
        for (int i = 0; i < TURNAMEN.size(); i++) {
            if (TURNAMEN.get(i).getGame() == game) {
                return true;
            }
        }
        return false;
    }

    public static List<Tournament> turnamenDiikuti(Team team) {
        java.util.ArrayList<Tournament> hasil = new java.util.ArrayList<>();
        for (int i = 0; i < TURNAMEN.size(); i++) {
            if (TURNAMEN.get(i).getPeserta().contains(team)) {
                hasil.add(TURNAMEN.get(i));
            }
        }
        return hasil;
    }

    /**
     * Mengisi 8 team Mobile Legends (masing-masing 5 inti + 1 cadangan) dan satu turnamen berkuota 8,
     * supaya demo tidak perlu mengetik 48 player satu per satu.
     * Hasilnya false kalau data sudah ada dan tidak ditimpa.
     */
    public static boolean isiDataContoh() {
        Game ml = cariGame("Mobile Legends");
        if (ml == null || !PLAYER.isEmpty() || !TEAM.isEmpty() || !TURNAMEN.isEmpty()) {
            return false;
        }
        String[] urutanRole = {"Jungler", "Roamer", "Mid Laner", "Gold Laner", "EXP Laner", "Roamer"};
        try {
            Tournament cup = new Tournament("MLBB Cup 2026", ml, 8);
            int nomor = 0;
            for (String namaTeam : TEAM_CONTOH) {
                Team team = new Team(idTeamBerikut(), namaTeam, ml);
                TEAM.addElement(team);
                for (String role : urutanRole) {
                    Player p = new Player(String.format("P%02d", nomor + 1), NICKNAME_CONTOH[nomor], ml, role);
                    PLAYER.addElement(p);
                    p.gabungTeam(team);
                    nomor++;
                }
                cup.terimaTeam(team);
            }
            TURNAMEN.addElement(cup);
            return true;
        } catch (AturanTurnamenException e) {
            // Data contoh disusun dari aturan yang sama, jadi seharusnya tidak pernah gagal
            throw new IllegalStateException("Data contoh tidak valid: " + e.getMessage(), e);
        }
    }
}
