package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.core.items.Item;
import nl.grapjeje.core.text.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@Getter
public class CtfFlag {
    private boolean placed = false;
    private Location location = null;

    @Setter
    private boolean stolen = false;
    @Setter
    private boolean dropped = false;

    public static void giveToPlayer(CtfPlayer p) {
        Team team = p.getTeam();

        ItemStack flagItem = Item.from(team == Team.RED ? Material.RED_WOOL : Material.BLUE_WOOL)
                .setName(team.getColorCode() + MessageUtil.capitalizeWords(team.name()) + "'s flag")
                .setLore(List.of("<gray>Right click to place the flag"))
                .setPersistentData("is_flag", PersistentDataType.BYTE, (byte) 1)
                .toBukkit();
        p.getPlayer().getInventory().addItem(flagItem);
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
        location = l.clone().add(0.5, 1, 0.5);
        Main.getInstance().getGame().getGameFlags().put(captainTeam, this);
        removeFromPlayer(p);

        p.getPlayer().sendMessage(MessageUtil.filterMessage("<green>The flag has been successfully placed!"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            CtfPlayer.loadOrCreatePlayerModelAsync(player).thenAccept(model -> {
                CtfPlayer cp = CtfPlayer.get(player.getUniqueId(), model);
                Team team = cp.getTeam();
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (team == captainTeam) {
                        CtfKit.get(cp).open();
                        cp.getPlayer().sendMessage(MessageUtil.filterMessage("<gold>Your team-flag has been successfully placed!"));
                    }
                    else cp.getPlayer().sendMessage(MessageUtil.filterMessage("<gray>The flag of team " + captainTeam.getColorCode() + captainTeam.name() + " <gray>has been successfully placed!"));
                });
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
    }

    @Deprecated
    public void place(Location loc, Team team) {
        placed = true;
        location = loc;
        Main.getInstance().getGame().getGameFlags().put(team, this);
    }

    public static boolean isCtfFlag(ItemStack item) {
        if (item == null) return false;
        return Item.from(item).hasPersistentData("is_flag");
    }
}
