package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.core.registry.AutoRegistry;
import nl.grapjeje.core.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.UUID;

@AutoRegistry
@Getter
public class CtfScoreboard {
    private final UUID uuid;
    private Player player;
    private CtfGame game;
    private BossBar bossBar;
    private CtfFlagBar flagBar;
    private int taskId = -1;

    CtfScoreboard(UUID uuid) {
        this.uuid = uuid;

        this.taskId = Main.getInstance().getScheduler().runTaskTimer(
                Main.getInstance(),
                this::tick,
                0,
                1
        ).getTaskId();
    }

    public static CtfScoreboard get(UUID uuid) {
        return Registry.get(
                CtfScoreboard.class,
                uuid.toString(),
                (args) -> new CtfScoreboard((UUID) args[0]),
                uuid
        );
    }

    public void create(Player player, CtfGame game) {
        this.player = player;
        this.game = game;
        
        // Unregister old scoreboard
        org.bukkit.scoreboard.Scoreboard mainBoard = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Objective oldObjective = mainBoard.getObjective("ctf_scoreboard");
        if (oldObjective != null) {
            oldObjective.unregister();
        }
        
        if (player.getScoreboard() != mainBoard) {
            org.bukkit.scoreboard.Scoreboard playerBoard = player.getScoreboard();
            org.bukkit.scoreboard.Objective playerObjective = playerBoard.getObjective("ctf_scoreboard");
            if (playerObjective != null) {
                playerObjective.unregister();
            }
        }
        player.setScoreboard(mainBoard);
        
        this.flagBar = new CtfFlagBar(player, game);
        
        int bluePoints = game.getPoints().getOrDefault(Team.BLUE, 0);
        int redPoints = game.getPoints().getOrDefault(Team.RED, 0);

        String title = "§9Blue: " + bluePoints + " §f- §cRed: " + redPoints;
        
        bossBar = Bukkit.createBossBar(
            title,
            getBarColor(bluePoints, redPoints),
            BarStyle.SOLID
        );
        
        bossBar.setProgress(getBarProgress(bluePoints, redPoints));
        bossBar.addPlayer(player);
        bossBar.setVisible(true);
    }

    private void update() {
        if (player == null || game == null || bossBar == null) return;
        
        int bluePoints = game.getPoints().getOrDefault(Team.BLUE, 0);
        int redPoints = game.getPoints().getOrDefault(Team.RED, 0);

        String title = "§9Blue: " + bluePoints + " §f- §cRed: " + redPoints;
        
        bossBar.setTitle(title);
        bossBar.setColor(getBarColor(bluePoints, redPoints));
        bossBar.setProgress(getBarProgress(bluePoints, redPoints));
        
        if (flagBar != null) {
            flagBar.update();
        }
    }

    public void remove(Player player) {
        if (player == null) return;

        this.player = null;
        this.game = null;

        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }

        if (bossBar != null) {
            bossBar.removePlayer(player);
            bossBar.removeAll();
            bossBar = null;
        }
        
        if (flagBar != null) {
            flagBar.remove();
            flagBar = null;
        }
        
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void tick() {
        update();
    }

    private BarColor getBarColor(int bluePoints, int redPoints) {
        if (bluePoints > redPoints) {
            return BarColor.BLUE;
        } else if (redPoints > bluePoints) {
            return BarColor.RED;
        } else {
            return BarColor.WHITE;
        }
    }

    private double getBarProgress(int bluePoints, int redPoints) {
        int total = bluePoints + redPoints;
        if (total == 0) {
            return 0.5;
        }
        double progress = (double) bluePoints / total;
        return Math.max(0.0, Math.min(1.0, progress));
    }
}