package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.listeners.CommandListener;
import nl.grapjeje.captureTheFlag.listeners.PlaceFlagListener;
import nl.grapjeje.captureTheFlag.listeners.VoteCaptainListener;

public class ListenerManager {

    public void init() {
        Main.getFramework().registerListener(CommandListener::new);

        // Game Listeners
        Main.getFramework().registerListener(VoteCaptainListener::new);
        Main.getFramework().registerListener(PlaceFlagListener::new);
    }
}
