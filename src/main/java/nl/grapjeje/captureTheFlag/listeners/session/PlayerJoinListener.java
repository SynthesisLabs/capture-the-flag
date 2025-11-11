package nl.grapjeje.captureTheFlag.listeners.session;

import fr.skytasul.glowingentities.GlowingEntities;
import nl.grapjeje.captureTheFlag.objects.CtfFlag;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.core.GlowUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static nl.grapjeje.captureTheFlag.enums.Team.RED;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.checkForFlagItem(e);
        this.setupGlowing(e);
    }

    private void checkForFlagItem(PlayerJoinEvent e) {
        for (ItemStack item : e.getPlayer().getInventory().getContents()) {
            if (CtfFlag.isCtfFlag(item)) item.setAmount(0);
        }
    }

    private void setupGlowing(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        GlowingEntities glowingEntities = GlowUtil.getInstance().getGlowingEntities();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                CtfPlayer.loadOrCreatePlayerModelAsync(player)
                        .thenAccept(model -> {
                            CtfPlayer ctfPlayer = CtfPlayer.get(onlinePlayer.getUniqueId(), model);
                            ChatColor glowColor = ctfPlayer.getTeam() == RED ? ChatColor.RED : ChatColor.BLUE;
                            try {
                                glowingEntities.setGlowing(onlinePlayer, player, glowColor);
                            } catch (ReflectiveOperationException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
            }
        }
    }
}
