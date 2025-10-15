package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.core.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CtfFlag {
    private boolean placed = false;
    private Location location = null;

    private boolean stolen = false;
    private boolean dropped = false;

    public static void giveToPlayer(CtfPlayer p) {
        Team team = p.getTeam();
        ItemStack item = (team == Team.RED) ? new ItemStack(Material.RED_WOOL) : new ItemStack(Material.BLUE_WOOL);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MessageUtil.filterMessage(team.getColorCode() + team.name() + " Flag"));
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
        Team captainTeam = p.getTeam();
        placed = true;
        location = l.set(l.getX() + 0.5, l.getY() + 1, l.getZ() + 0.5);
        Main.getInstance().getGame().getGameFlags().put(captainTeam, this);
        removeFromPlayer(p);
        p.getPlayer().sendMessage(MessageUtil.filterMessage("<green>The flag has been successfully placed!"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            CtfPlayer cp = CtfPlayer.get(player);
            if (cp == p) continue;
            Team team = cp.getTeam();
            if (team == captainTeam) cp.getPlayer().sendMessage(MessageUtil.filterMessage("<gold>Your team-flag has been successfully placed!"));
            else cp.getPlayer().sendMessage(MessageUtil.filterMessage("<gray>The flag of team " + team.getColorCode() + team.name() + " <gray>has been successfully placed!"));
        }
    }

    @Deprecated
    public void place(Location loc, Team team) {
        placed = true;
        location = loc;
        Main.getInstance().getGame().getGameFlags().put(team, this);
    }

    public static boolean isCtfFlag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("ctf", "is_flag");

        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
