package nl.grapjeje.captureTheFlag.listeners;

import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        CtfPlayer.loadOrCreatePlayerModelAsync(player)
                .thenAcceptAsync(model -> {
                    CtfPlayer ctfPlayer = CtfPlayer.get(player.getUniqueId(), model);
                    boolean inGame;
                    if (ctfPlayer.getTeam() == Team.RED || ctfPlayer.getTeam() == Team.BLUE)
                        inGame = true;
                    else inGame = false;

                    if (!inGame) return;
                    ctfPlayer.setDeath(true);
                    // TODO: Add to the database
                });
    }
}
