package nl.grapjeje.captureTheFlag.objects;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.core.registry.AutoRegistry;
import nl.grapjeje.core.registry.Registry;
import nl.grapjeje.core.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.UUID;

@AutoRegistry
@Getter
public class CtfScoreboard {
    private static final String OBJECTIVE_NAME = "ctf_scoreboard";

    private final UUID uuid;
    private Player player;
    private CtfGame game;

    CtfScoreboard(UUID uuid) {
        this.uuid = uuid;

        Main.getInstance().getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1);
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
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        String bluePoints = String.valueOf(game.getPoints().getOrDefault(Team.BLUE, 0));
        String redPoints = String.valueOf(game.getPoints().getOrDefault(Team.RED, 0));

        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy", Component.text("§6§lCAPTURE THE FLAG"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.numberFormat(NumberFormat.blank());

//        obj.getScore(MessageUtil.filterMessageStringLegacy("<gray>")).setScore(8);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<white>" + player.getName())).setScore(7);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<gray>")).setScore(6);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<white>Score:")).setScore(5);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<green>" + bluePoints + "<white> - " + "<red>" + redPoints)).setScore(4);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<gray>")).setScore(3);

        obj.getScore("§7 ").setScore(8);
        obj.getScore("§f" + player.getName()).setScore(7);
        obj.getScore("§7  ").setScore(6);
        obj.getScore("§fScore:").setScore(5);
        obj.getScore("§9" + bluePoints + "§f - §c" + redPoints).setScore(4);
        obj.getScore("§7   ").setScore(3);

        player.setScoreboard(board);
    }

    private void update() {
        if (player == null || game == null) return;
        
        Scoreboard board = player.getScoreboard();

        Objective oldObj = board.getObjective(OBJECTIVE_NAME);
        if (oldObj != null) {
            oldObj.unregister();
        }
        
        String bluePoints = String.valueOf(game.getPoints().getOrDefault(Team.BLUE, 0));
        String redPoints = String.valueOf(game.getPoints().getOrDefault(Team.RED, 0));

        Objective obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy", "§6§lCAPTURE THE FLAG");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.numberFormat(NumberFormat.blank());

//        obj.getScore(MessageUtil.filterMessageStringLegacy("<gray>")).setScore(8);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<white>" + player.getName())).setScore(7);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<gray>")).setScore(6);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<white>Score:")).setScore(5);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<blue>" + bluePoints + "<white> - " + "<red>" + redPoints)).setScore(4);
//        obj.getScore(MessageUtil.filterMessageStringLegacy("<gray>")).setScore(3);

        obj.getScore("§7 ").setScore(8);
        obj.getScore("§f" + player.getName()).setScore(7);
        obj.getScore("§7  ").setScore(6);
        obj.getScore("§fScore:").setScore(5);
        obj.getScore("§9" + bluePoints + "§f - §c" + redPoints).setScore(4);
        obj.getScore("§7   ").setScore(3);
    }

    public void remove(Player player, CtfGame game) {
        if (player == null || game == null) return;
        Objective board = player.getScoreboard().getObjective(OBJECTIVE_NAME);
        if(board != null) board.unregister();
    }

    public void tick() {
        update();
    }
}