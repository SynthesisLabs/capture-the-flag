package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.listeners.CommandListener;
import nl.grapjeje.captureTheFlag.listeners.PlaceFlagListener;
import nl.grapjeje.captureTheFlag.listeners.VoteCaptainListener;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.captureTheFlag.objects.CtfScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerManager {

    public void init() {
        // Existing listeners
        this.registerEventListener(new CommandListener());
        this.registerEventListener(new VoteCaptainListener());
        this.registerEventListener(new PlaceFlagListener());

        // Add join/quit listener
        this.registerEventListener(new PlayerJoinQuitListener());
    }

    private void registerEventListener(Listener eventListener) {
        Main.getInstance().getServer().getPluginManager().registerEvents(eventListener, Main.getInstance());
    }

    // Inner class to handle join/quit
    private static class PlayerJoinQuitListener implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            CtfPlayer ctfPlayer = CtfPlayer.get(event.getPlayer());
            CtfScoreboardManager.create(ctfPlayer);

            if (Main.getInstance().getGame() != null) {
                Main.getInstance().getGame().getPlayers().add(ctfPlayer);
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            CtfScoreboardManager.remove(event.getPlayer());
        }
    }
}
