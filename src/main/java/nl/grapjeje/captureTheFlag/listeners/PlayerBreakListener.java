package nl.grapjeje.captureTheFlag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }
}
