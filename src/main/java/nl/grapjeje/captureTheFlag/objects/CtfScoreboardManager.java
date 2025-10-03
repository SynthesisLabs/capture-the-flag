package nl.grapjeje.captureTheFlag.objects;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CtfScoreboardManager {
    private static final Map<UUID, CtfScoreboard> boards = new HashMap<>();

    public static void create(CtfPlayer ctfPlayer) {
        CtfScoreboard board = new CtfScoreboard(ctfPlayer);
        boards.put(ctfPlayer.getUuid(), board);
    }

    public static void update(CtfPlayer ctfPlayer) {
        CtfScoreboard board = boards.get(ctfPlayer.getUuid());
        if (board != null) {
            board.update(ctfPlayer);
        }
    }

    public static void remove(Player player) {
        boards.remove(player.getUniqueId());
        player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
    }

    public static void clearAll() {
        boards.clear();
    }
}
