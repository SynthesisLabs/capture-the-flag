package nl.grapjeje.captureTheFlag.listeners;

import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfFlag;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceFlagListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null) return;
        if (!CtfFlag.isCtfFlag(item)) return;

        Player p = e.getPlayer();
        CtfPlayer.loadOrCreatePlayerModelAsync(p)
                .thenAccept(model -> {
                    CtfPlayer ctfPlayer = CtfPlayer.get(p.getUniqueId(), model);
                    if (!ctfPlayer.isCaptain()) return;
                    e.setCancelled(true);

                    Team team = ctfPlayer.getTeam();
                    final CtfFlag[] flag = new CtfFlag[1];
                    Block clickedBlock = e.getClickedBlock();
                    if (clickedBlock == null) return;

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        if (Main.getInstance().getGame().getGameFlags().containsKey(team)) {
                            flag[0] = Main.getInstance().getGame().getGameFlags().get(team);
                            if (flag[0].isPlaced()) CtfFlag.removeFromPlayer(ctfPlayer);
//                          else flag.place(ctfPlayer, clickedBlock.getLocation());
                        } else {
                            flag[0] = new CtfFlag();
                            flag[0].place(ctfPlayer, clickedBlock.getLocation());
                        }
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}
