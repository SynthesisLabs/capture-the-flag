package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.GameStatus;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

@Getter
public class CtfGame {
    private final List<CtfPlayer> players = new ArrayList<>();

    @Setter
    private GameStatus status = GameStatus.STOPPED;

    public CtfGame() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(CtfPlayer.get(p));
        }
        Main.getInstance().getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1);
    }

    private void tick() {
        this.start();
    }

    private final Map<Team, Map<UUID, Integer>> votes = new HashMap<>();

    public void start() {
        if (status == GameStatus.STARTED) return;
        this.setStatus(GameStatus.STARTED);
        this.assignTeams();

        votes.put(Team.BLUE, new HashMap<>());
        votes.put(Team.RED, new HashMap<>());

        for (CtfPlayer ctfPlayer : this.players) {
            this.openVoteMenu(ctfPlayer);
        }
        Main.getInstance().getScheduler().runTaskLater(Main.getInstance(), this::pickCaptains, 20 * 15);
    }

    private void openVoteMenu(CtfPlayer ctfPlayer) {
        Player player = ctfPlayer.getPlayer();

        List<Player> teamMates = this.players.stream()
                .filter(p -> p.getTeam() == ctfPlayer.getTeam())
                // .filter(p -> p != ctfPlayer)
                .map(CtfPlayer::getPlayer)
                .toList();

        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Vote your captain"));

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        int slot = 0;
        for (Player mate : teamMates) {
            ItemStack skull = createPlayerHead(mate);
            inv.setItem(slot++, skull);
        }

        player.openInventory(inv);
    }

    private ItemStack createPlayerHead(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        meta.displayName(Component.text(player.getName()));
        skull.setItemMeta(meta);
        return skull;
    }

    public void addVote(Team team, UUID playerId) {
        votes.get(team).merge(playerId, 1, Integer::sum);
    }

    private void pickCaptains() {
        for (Team team : List.of(Team.BLUE, Team.RED)) {
            Map<UUID, Integer> teamVotes = votes.get(team);
            if (teamVotes == null || teamVotes.isEmpty()) continue;
            UUID winner = teamVotes.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (winner != null) {
                Player captain = Bukkit.getPlayer(winner);
                if (captain != null) {
                    CtfPlayer.get(captain).setCaptain(true);
                    for (CtfPlayer p : this.players) {
                        if (p.getTeam() == Team.NONE) continue;
                        p.getPlayer().sendMessage(MessageUtil.filterMessage(
                                "<primary><bold>\uD83C\uDFC6 " + captain.getName() + "<!bold> has been chosen as captain for team <bold>" + team
                        ));
                    }
                    captain.sendMessage(
                            MessageUtil.filterMessage("<gray><bold>🎖 <!bold>You are now the captain of team <bold>" + team + "!")
                    );
                }
            }
        }
    }

    private void broadcastTeam(Team team, Component message) {
        for (CtfPlayer ctfPlayer : this.players) {
            if (ctfPlayer.getTeam() == team) {
                ctfPlayer.getPlayer().sendMessage(message);
            }
        }
    }

    private void assignTeams() {
        int blueCount = 0;
        int redCount = 0;

        for (CtfPlayer player : this.players) {
            if (player.getTeam() != Team.NONE) continue;

            if (blueCount <= redCount) {
                player.setTeam(Team.BLUE);
                blueCount++;
            } else {
                player.setTeam(Team.RED);
                redCount++;
            }
        }
    }
}
