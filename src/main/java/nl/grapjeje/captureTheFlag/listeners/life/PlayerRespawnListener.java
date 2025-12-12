package nl.grapjeje.captureTheFlag.listeners.life;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfFlag;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class PlayerRespawnListener implements Listener {

    private final int respawnTimeInSeconds = 7;

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            player.setGameMode(GameMode.SPECTATOR);

            CtfPlayer.loadOrCreatePlayerModelAsync(player)
                    .thenAccept(model -> {
                        CtfPlayer ctfPlayer = CtfPlayer.get(player.getUniqueId(), model);
                        boolean inGame = ctfPlayer.getTeam() == Team.RED || ctfPlayer.getTeam() == Team.BLUE;
                        if (!inGame) return;

                        new BukkitRunnable() {
                            int counter = respawnTimeInSeconds;

                            @Override
                            public void run() {
                                if (!player.isOnline()) {
                                    this.cancel();
                                    return;
                                }

                                if (counter <= 3 && counter > 0)
                                    player.showTitle(Title.title(
                                            MessageUtil.filterMessage("<primary>" + counter),
                                            Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));

                                player.sendActionBar(MessageUtil.filterMessage("<primary>Aftellen: <bold>" + counter + "s<!bold>"));

                                if (counter <= 0) {
                                    CtfFlag flag = Main.getInstance().getGame().getGameFlags().get(ctfPlayer.getTeam());
                                    Location flagLoc = flag.getLocation();
                                    if (flagLoc == null) return;
                                    player.teleport(flagLoc.add(0, 1, 0));

                                    player.setGameMode(GameMode.SURVIVAL);
                                    ctfPlayer.setDeath(false);
                                    this.cancel();
                                    player.sendActionBar(MessageUtil.filterMessage("<green>Je bent gerespawned!"));
                                }
                                counter--;
                            }
                        }.runTaskTimer(Main.getInstance(), 0L, 20L);
                    });
        });
    }

}
