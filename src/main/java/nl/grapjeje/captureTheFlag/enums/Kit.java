package nl.grapjeje.captureTheFlag.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Kit {
    SCOUT(Material.LEATHER_HELMET, "<blue>"),
    HEAVY(Material.SHIELD, "<green>"),
    SOLDIER(Material.IRON_SWORD, "<pink>"),
    SNIPER(Material.BOW, "<purple>"),
    BESERKER(Material.IRON_AXE, "<yellow>");

    final Material material;
    final String colorCode;
}
