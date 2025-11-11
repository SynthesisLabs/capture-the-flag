package nl.grapjeje.captureTheFlag.listeners.life;

import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

import static nl.grapjeje.captureTheFlag.enums.Team.BLUE;
import static nl.grapjeje.captureTheFlag.enums.Team.RED;

public class PlayerDeathListener implements Listener {

    private final Map<String, Boolean> deathMessages = Map.ofEntries(
            Map.entry("dropped the flag and met their end thanks to", true),
            Map.entry("got flagged out by", false),
            Map.entry("was intercepted mid-flag by", true),
            Map.entry("lost the flag and their life to", true),
            Map.entry("was sent back to spawn by", false),
            Map.entry("had their flag dreams crushed by", true),
            Map.entry("got ambushed while carrying the flag by", true),
            Map.entry("was denied glory by", false),
            Map.entry("was captured while flag-running by", true),
            Map.entry("tripped over the flag thanks to", true),
            Map.entry("was flagged down by", false),
            Map.entry("couldn't escape the flag trap set by", false),
            Map.entry("was returned to base by", false),
            Map.entry("dropped the flag in style thanks to", true),
            Map.entry("was yeeted into the void with the flag by", true),
            Map.entry("faceplanted while stealing the flag thanks to", true),
            Map.entry("was roasted trying to carry the flag by", true),
            Map.entry("got sabotaged mid-flag by", true),
            Map.entry("was outplayed and sent home by", false),
            Map.entry("tripped on their own ego while holding the flag thanks to", true),
            Map.entry("was eliminated in a heroic fail by", false),
            Map.entry("got ninja-ed while grabbing the flag by", true),
            Map.entry("was caught napping with the flag by", true),
            Map.entry("failed the flag heist thanks to", true),
            Map.entry("was memed into oblivion while holding the flag by", true)
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        e.setShowDeathMessages(false);

        CtfPlayer.loadOrCreatePlayerModelAsync(player)
                .thenAcceptAsync(model -> {
                    CtfPlayer ctfPlayer = CtfPlayer.get(player.getUniqueId(), model);
                    boolean inGame = ctfPlayer.getTeam() == RED || ctfPlayer.getTeam() == BLUE;

                    if (!inGame) return;
                    ctfPlayer.setDeath(true);

                    boolean hasFlag = ctfPlayer.isHasFlag();
                    // Drop flag
                    if (hasFlag) {
                        ctfPlayer.setHasFlag(false);
                        Team flagTeam = (ctfPlayer.getTeam() == RED ? BLUE : RED);
                        Location dropLoc = player.getLocation().clone();

                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            CtfGame game = Main.getInstance().getGame();
                            if (game == null) return;

                            CtfGame.DroppedFlag df = game.new DroppedFlag(dropLoc, flagTeam);
                            game.getDroppedFlags().put(flagTeam, df);

                            Bukkit.broadcast(MessageUtil.filterMessage("<gray>The <bold>" + flagTeam + "<!bold><gray> flag was dropped!"));
                        });
                    }

                    // Add kill
                    DamageSource source = e.getDamageSource();
                    if (source.getCausingEntity() instanceof Player killer) {
                        CtfPlayer.loadOrCreatePlayerModelAsync(killer)
                                .thenAcceptAsync(killerModel -> {
                                    CtfPlayer killerCtfPlayer = CtfPlayer.get(killer.getUniqueId(), killerModel);
                                    killerCtfPlayer.addKill();
                                    killerCtfPlayer.save();

                                    // Death message
                                    String deathMessage = deathMessages.entrySet().stream()
                                            .filter(entry -> hasFlag || entry.getValue())
                                            .map(Map.Entry::getKey)
                                            .toList()
                                            .get((int) (Math.random() * deathMessages.size()));

                                    Bukkit.broadcast(MessageUtil.filterMessage(String.format("<warning>💀 %s %s %s", player.getName(), deathMessage, killer.getName())));
                                });
                    }

                    // Add death
                    ctfPlayer.addDeath();
                    ctfPlayer.save();
                });
    }
}
