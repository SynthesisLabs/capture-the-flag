package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.listeners.CommandListener;
import nl.grapjeje.captureTheFlag.listeners.PlaceFlagListener;
import nl.grapjeje.captureTheFlag.listeners.VoteCaptainListener;
import org.bukkit.event.Listener;

public class ListenerManager {

    public void init() {
        this.registerEventListener(new CommandListener());

        // Game Listeners
        this.registerEventListener(new VoteCaptainListener());
        this.registerEventListener(new PlaceFlagListener());
    }

    private void registerEventListener(Listener eventListener) {
        Main.getInstance().getServer().getPluginManager().registerEvents(eventListener, Main.getInstance());
    }
}
