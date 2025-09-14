package nl.grapjeje.captureTheFlag;

import org.bukkit.event.Listener;

public class ListenerManager {

    public void init() {

    }

    public void registerEventListener(Listener eventListener) {
        Main.getInstance().getServer().getPluginManager().registerEvents(eventListener, Main.getInstance());
    }
}
