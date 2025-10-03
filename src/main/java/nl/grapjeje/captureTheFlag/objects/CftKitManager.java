package nl.grapjeje.captureTheFlag.objects;

import nl.grapjeje.captureTheFlag.enums.Kit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CftKitManager {

    private final Map<UUID, Kit> playerKits = new HashMap<>();

    public void setKit(CtfPlayer playerData, Kit kit) {
        playerKits.put(playerData.getUuid(), kit);

        CftKit cftKit = CftKit.getKit(kit);
        if (cftKit != null) {
            cftKit.applyTo(playerData);
        }

        playerData.setKit(kit);
    }

    public Optional<Kit> getKit(CtfPlayer playerData) {
        return Optional.ofNullable(playerKits.get(playerData.getUuid()));
    }

    public boolean hasKit(CtfPlayer playerData) {
        return playerKits.containsKey(playerData.getUuid());
    }

    public void removeKit(CtfPlayer playerData) {
        playerKits.remove(playerData.getUuid());
    }

    public Kit[] getAvailableKits() {
        return Kit.values();
    }
}
