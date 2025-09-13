package nl.grapjeje.captureTheFlag.exeptions;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String msg, UUID uuid) {
        super("Player not found or not online: " + msg + " (" + uuid + ")");
    }

    public PlayerNotFoundException(UUID uuId) {
        super("Player not found or not online (" + uuId + ")");
    }
}
