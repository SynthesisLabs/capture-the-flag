package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.listeners.*;

public class ListenerManager {

    public void init() {
        Main.getFramework().registerListener(CommandListener::new);

        // Game Listeners
        Main.getFramework().registerListener(VoteCaptainListener::new);
        Main.getFramework().registerListener(PlaceFlagListener::new);
        Main.getFramework().registerListener(PlayerLeaveListener::new);
        Main.getFramework().registerListener(PlayerJoinListener::new);
        Main.getFramework().registerListener(PlayerDeathListener::new);
    }
}
