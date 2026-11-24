package arenaforge;

/** Data player sederhana untuk ditampilkan di JList. */
public class Player {
    private final String id;
    private final String nickname;
    private final String game;
    private final String role;

    public Player(String id, String nickname, String game, String role) {
        this.id = id;
        this.nickname = nickname;
        this.game = game;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGame() {
        return game;
    }

    public String getRole() {
        return role;
    }

    /** Teks yang muncul di JList. */
    @Override
    public String toString() {
        return id + "   |   " + nickname + "   |   " + game + " - " + role;
    }
}
