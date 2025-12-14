package nl.grapjeje.captureTheFlag.listeners.session;

import fr.skytasul.glowingentities.GlowingEntities;
import nl.grapjeje.captureTheFlag.objects.CtfFlag;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import nl.grapjeje.core.GlowUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static nl.grapjeje.captureTheFlag.enums.Team.RED;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        CtfPlayer.loadOrCreatePlayerModelAsync(player)
                .thenAcceptAsync(model -> {
                    CtfPlayer ctfPlayer = CtfPlayer.get(player.getUniqueId(), model);
                    Objects.requireNonNull(player.getPlayer()).clearActivePotionEffects();
                    player.getInventory().clear();
                    ctfPlayer.getScoreboard().remove(player);
                    player.setGameMode(GameMode.SPECTATOR);
                });

        player.teleport(player.getWorld().getSpawnLocation());

        this.sendJoinMessage(e);
        this.checkForFlagItem(e);
        this.setupGlowing(e);
    }

    private void sendJoinMessage(PlayerJoinEvent e) {
        e.joinMessage(MessageUtil.filterMessage("<gray>[<green>+<gray>] <white>" + e.getPlayer().getName()));
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
