package nl.grapjeje.captureTheFlag.objects;

import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

@Getter
public class CtfFlag {
    private boolean placed = false;
    private Location location = null;

    public static void giveToPlayer(CtfPlayer p) {
//        ItemStack item = new ItemStack(Material.BLACK_WOOL);
//        ItemMeta meta = item.getItemMeta();
//
//        var cmd = CustomModelData.customModelData()
//                .addFloat(1.23f)
//                .addString("ctf_flag")
//                .addColor(Color.BLACK)
//                .build();
//        item.setCustomModelData(cmd);
//        meta.setCustomModelDataComponent(cmd);
//        item.setItemMeta(meta);
//
//
//        p.getPlayer().getInventory().addItem(item);
    }

    public static void removeFromPlayer(CtfPlayer p) {
        var inv = p.getPlayer().getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || !item.hasItemMeta()) continue;
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasCustomModelDataComponent()) continue;
            CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            if (cmd.getStrings().contains("ctf_flag")) inv.setItem(i, null);
        }
    }

    public void place(CtfPlayer p, Location l) {
        p.getPlayer().sendMessage("You have been placed on this location!");
        removeFromPlayer(p);
    }

    public static boolean isCtfFlag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();

        if (!meta.hasCustomModelDataComponent()) return false;
        org.bukkit.inventory.meta.components.CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        return cmd.getStrings().contains("ctf_flag");
    }
}
