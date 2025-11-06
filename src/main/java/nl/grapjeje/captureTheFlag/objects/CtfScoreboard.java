package nl.grapjeje.captureTheFlag.objects;

import nl.grapjeje.captureTheFlag.enums.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CtfScoreboard {
    private static final String OBJECTIVE_NAME = "ctf_scoreboard";

    public static void create(Player player, CtfGame game) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        String bluePoints = String.valueOf(game.getPoints().getOrDefault(Team.BLUE, 0));
        String redPoints = String.valueOf(game.getPoints().getOrDefault(Team.RED, 0));
        String message = bluePoints + " - " + redPoints;

        Scoreboard board = manager.getNewScoreboard();
        
        //  Objective obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy", message);
        Objective obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy", player.getName());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        player.setScoreboard(board);
    }
}