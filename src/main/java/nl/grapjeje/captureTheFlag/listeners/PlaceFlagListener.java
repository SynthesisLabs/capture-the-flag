package nl.grapjeje.captureTheFlag.listeners;

import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfFlag;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlaceFlagListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null) return;
        if (!CtfFlag.isCtfFlag(item)) return;

        Player p = e.getPlayer();
        CtfPlayer ctfPlayer = CtfPlayer.get(p);
        if (!ctfPlayer.isCaptain()) return;
        e.setCancelled(true);

        Team team = ctfPlayer.getTeam();
        CtfFlag flag;
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) return;

        if (Main.getInstance().getGame().getGameFlags().containsKey(team)) {
            flag = Main.getInstance().getGame().getGameFlags().get(team);
            if (flag.isPlaced()) CtfFlag.removeFromPlayer(ctfPlayer);
//            else flag.place(ctfPlayer, clickedBlock.getLocation());
        } else {
            flag = new CtfFlag();
            flag.place(ctfPlayer, clickedBlock.getLocation());
        }
    }
}
