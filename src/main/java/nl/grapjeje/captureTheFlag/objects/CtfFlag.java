package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.grapjeje.captureTheFlag.enums.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CtfFlag {
    private boolean placed = false;
    private Location location = null;

    public static void giveToPlayer(CtfPlayer p) {
        Team team = p.getTeam();
        ItemStack item = (team == Team.RED) ? new ItemStack(Material.RED_WOOL) : new ItemStack(Material.BLUE_WOOL);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(team.getColorCode() + team.name() + " Flag"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to place the flag").color(NamedTextColor.GRAY));
        meta.lore(lore);

        NamespacedKey key = new NamespacedKey("ctf", "is_flag");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

        item.setItemMeta(meta);
        p.getPlayer().getInventory().addItem(item);
    }

    public static void removeFromPlayer(CtfPlayer p) {
        var inv = p.getPlayer().getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (isCtfFlag(item)) inv.setItem(i, null);
        }
    }

    public void place(CtfPlayer p, Location l) {
        p.getPlayer().sendMessage("You have been placed on this location!");
        removeFromPlayer(p);
    }

    public static boolean isCtfFlag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("ctf", "is_flag");

        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
