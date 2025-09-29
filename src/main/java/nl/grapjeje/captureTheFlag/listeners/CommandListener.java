package nl.grapjeje.captureTheFlag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandListener implements Listener {

    private static final List<String> commands = new ArrayList<>();

    static {
        commands.add("/minecraft:help");
        commands.add("/minecraft:me");
        commands.add("/minecraft:msg");
        commands.add("/minecraft:teammsg");
        commands.add("/minecraft:tell");
        commands.add("/minecraft:tm");
        commands.add("/minecraft:trigger");
        commands.add("/minecraft:w");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        for (String cmd : commands) {
            if (message.startsWith(cmd)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
