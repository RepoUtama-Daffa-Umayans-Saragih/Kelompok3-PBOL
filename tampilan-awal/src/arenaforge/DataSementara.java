package arenaforge;

import javax.swing.DefaultListModel;

/**
 * Data yang disimpan di memori selama aplikasi berjalan.
 * Nanti diganti dengan database MySQL.
 */
public final class DataSementara {
    public static final String[] GAME = {"Mobile Legends", "Free Fire", "Tekken"};

    /** Player yang sudah diregistrasi, langsung dipakai sebagai model JList. */
    public static final DefaultListModel<Player> PLAYER = new DefaultListModel<>();

    private DataSementara() {
    }

    public static String[] roleUntuk(String game) {
        return switch (game) {
            case "Mobile Legends" -> new String[] {"Jungler", "Roamer", "Mid Laner", "Gold Laner", "EXP Laner"};
            case "Free Fire" -> new String[] {"Rusher", "Sniper", "Support", "IGL"};
            case "Tekken" -> new String[] {"Solo"};
            default -> new String[0];
        };
    }

    public static String rosterUntuk(String game) {
        return switch (game) {
            case "Mobile Legends" -> "5 inti + 2 cadangan";
            case "Free Fire" -> "4 inti + 2 cadangan";
            case "Tekken" -> "1 pemain (solo)";
            default -> "-";
        };
    }

    public static boolean idSudahDipakai(String id) {
        for (int i = 0; i < PLAYER.size(); i++) {
            if (PLAYER.get(i).getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
