package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.listeners.CommandListener;
import org.bukkit.event.Listener;

public class ListenerManager {

    public void init() {
        this.registerEventListener(new CommandListener());
    }

    private void registerEventListener(Listener eventListener) {
        Main.getInstance().getServer().getPluginManager().registerEvents(eventListener, Main.getInstance());
    }
}
