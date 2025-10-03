package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import nl.grapjeje.captureTheFlag.enums.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CtfScoreboard {
    @Getter
    private final Scoreboard scoreboard;
    private final Objective objective;

    public CtfScoreboard(CtfPlayer ctfPlayer) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();

        this.objective = scoreboard.registerNewObjective(
                "ctf", Criteria.DUMMY,
                ChatColor.GOLD + "" + ChatColor.BOLD + "Capture The Flag"
        );
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        update(ctfPlayer);
        ctfPlayer.getPlayer().setScoreboard(this.scoreboard);
    }

    public void update(CtfPlayer ctfPlayer) {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        int line = 8;

        Team team = ctfPlayer.getTeam();
        addLine(ChatColor.YELLOW + "Team: " + getTeamName(team), line--);

        if (ctfPlayer.isCaptain()) {
            addLine(ChatColor.AQUA + "★ Captain ★", line--);
        }

        addLine(ChatColor.RED + "Kills: " + ChatColor.WHITE + ctfPlayer.getKills(), line--);
        addLine(ChatColor.DARK_RED + "Deaths: " + ChatColor.WHITE + ctfPlayer.getDeaths(), line--);
        addLine(ChatColor.GOLD + "Coins: " + ChatColor.WHITE + (int) ctfPlayer.getCoins(), line--);

        addLine(" ", line--);
        addLine(ChatColor.GRAY + "ctf.server.nl", line--);
    }

    private void addLine(String text, int line) {
        Score score = this.objective.getScore(text);
        score.setScore(line);
    }

    private String getTeamName(Team team) {
        return switch (team) {
            case RED -> ChatColor.RED + "RED";
            case BLUE -> ChatColor.BLUE + "BLUE";
            default -> ChatColor.GRAY + "NONE";
        };
    }
}
