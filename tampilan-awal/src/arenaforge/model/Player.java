package arenaforge.model;

/** Profil player: identitas, role, team, dan statistik pertandingan. Atribut private, diakses lewat getter. */
public class Player {
    private final String id;
    private final String nickname;
    private final Game game;
    private String role;
    private Team team;
    private int menang;
    private int seri;
    private int kalah;

    public Player(String id, String nickname, Game game, String role) {
        if (id == null || id.isBlank() || nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("ID Player dan nickname wajib diisi.");
        }
        this.id = id.trim();
        this.nickname = nickname.trim();
        this.game = game;
        setRole(role);
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Game getGame() {
        return game;
    }

    public String getRole() {
        return role;
    }

    public Team getTeam() {
        return team;
    }

    public int getMenang() {
        return menang;
    }

    public int getSeri() {
        return seri;
    }

    public int getKalah() {
        return kalah;
    }

    public int getMain() {
        return menang + seri + kalah;
    }

    /** Persentase menang dari semua pertandingan yang dimainkan, 0 kalau belum pernah main. */
    public int persenMenang() {
        return getMain() == 0 ? 0 : Math.round(menang * 100f / getMain());
    }

    /** Role harus salah satu role milik game player ini. */
    public void setRole(String role) {
        if (role == null || !game.roleValid(role)) {
            throw new IllegalArgumentException("Role \"" + role + "\" tidak ada di game " + game.getNama() + ".");
        }
        this.role = role;
    }

    /** Bergabung ke sebuah team. Aturannya (game sama, roster tidak penuh, dst.) diperiksa oleh Team. */
    public void gabungTeam(Team t) throws AturanTurnamenException {
        t.tambahPlayer(this);
    }

    /** Dipanggil oleh Team saja saat player masuk atau keluar roster. */
    void tetapkanTeam(Team t) {
        this.team = t;
    }

    public void updateStatistik(Hasil hasil) {
        switch (hasil) {
            case MENANG -> menang++;
            case SERI -> seri++;
            case KALAH -> kalah++;
        }
    }

    /** Membatalkan satu hasil yang sudah dicatat (dipakai saat skor dikoreksi). */
    public void batalkanStatistik(Hasil hasil) {
        switch (hasil) {
            case MENANG -> menang--;
            case SERI -> seri--;
            case KALAH -> kalah--;
        }
    }

    /** Teks yang muncul di JList. */
    @Override
    public String toString() {
        return id + "   |   " + nickname + "   |   " + game.getNama() + " - " + role;
    }
}
