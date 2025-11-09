package nl.grapjeje.captureTheFlag.listeners;

import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        CtfPlayer.loadOrCreatePlayerModelAsync(player)
                .thenAccept(model -> {
                    CtfPlayer ctfPlayer = CtfPlayer.get(player.getUniqueId(), model);
                    if (ctfPlayer == null || !Main.getInstance().getGame().getPlayers().contains(ctfPlayer)) return;
                    Main.getInstance().getGame().getPlayers().remove(ctfPlayer);
                });
    }
}
