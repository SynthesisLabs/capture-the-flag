package nl.grapjeje.captureTheFlag.listeners;

import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class VoteCaptainListener implements Listener {

    @EventHandler
    public void onVoteClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (!e.getView().title().equals(net.kyori.adventure.text.Component.text("Vote your captain"))) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;
        SkullMeta meta = (SkullMeta) clicked.getItemMeta();
        if (meta == null || meta.getOwningPlayer() == null) return;
        Player voted = meta.getOwningPlayer().getPlayer();
        if (voted == null) return;

        Team team = Main.getInstance().getGame().getPlayers().stream()
                .filter(p -> p.getPlayer().equals(player))
                .findFirst()
                .map(CtfPlayer::getTeam)
                .orElse(null);

        if (team != null) {
            Main.getInstance().getGame().addVote(team, voted.getUniqueId());
            player.closeInventory();
            player.sendMessage(MessageUtil.filterMessage(
                    "<gray>You voted for <primary>" + voted.getName()
            ));
        }
    }
}
