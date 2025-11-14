package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.listeners.*;
import nl.grapjeje.captureTheFlag.listeners.life.PlayerDeathListener;
import nl.grapjeje.captureTheFlag.listeners.life.PlayerRespawnListener;
import nl.grapjeje.captureTheFlag.listeners.session.PlayerJoinListener;
import nl.grapjeje.captureTheFlag.listeners.session.PlayerLeaveListener;

public class ListenerManager {

    public void init() {
        Main.getFramework().registerListener(CommandListener::new);

        // Game Listeners
        Main.getFramework().registerListener(PlaceFlagListener::new);
        Main.getFramework().registerListener(PlayerLeaveListener::new);
        Main.getFramework().registerListener(PlayerJoinListener::new);
        Main.getFramework().registerListener(PlayerDeathListener::new);
        Main.getFramework().registerListener(PlayerRespawnListener::new);
    }
}
