package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.GameStatus;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static nl.grapjeje.captureTheFlag.enums.Team.BLUE;
import static nl.grapjeje.captureTheFlag.enums.Team.RED;

@Getter
public class CtfGame {
    private final List<CtfPlayer> players = new ArrayList<>();

    @Setter
    private GameStatus status = GameStatus.STOPPED;

    private final Map<Team, CtfFlag> gameFlags = new HashMap<>();

    public CtfGame() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(CtfPlayer.get(p));
        }
        Main.getInstance().getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1);
    }

    private void tick() {
        this.start();
        this.showFlag();
    }

    private void showFlag() {
        if (gameFlags.isEmpty()) return;
        for (Map.Entry<Team, CtfFlag> entry : gameFlags.entrySet()) {
            CtfFlag flag = entry.getValue();
            Team team = entry.getKey();
            Location baseLoc = flag.getLocation();

            if (!flag.isPlaced() || baseLoc == null) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (baseLoc == null) return;
                    String hologramPrefix = "ctf_flag_hologram_" + team.name() + "_";
                    for (Entity e : baseLoc.getWorld().getNearbyEntities(baseLoc, 3.0, 3.0, 3.0)) {
                        if (e instanceof ArmorStand as &&
                                as.getCustomName() != null &&
                                as.getCustomName().startsWith(hologramPrefix)) {
                            as.remove();
                        }
                    }
                });
                continue;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                final double radius = 2;
                final int points = 35;
                final double cx = baseLoc.getX();
                final double cy = baseLoc.getY();
                final double cz = baseLoc.getZ();

                List<Location> ring = new ArrayList<>(points);
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    ring.add(new Location(baseLoc.getWorld(), cx + x, cy, cz + z));
                }

                Color color;
                Material woolType;
                switch (team) {
                    case BLUE -> {
                        color = Color.fromRGB(0, 150, 255);
                        woolType = Material.BLUE_WOOL;
                    }
                    case RED -> {
                        color = Color.fromRGB(255, 50, 50);
                        woolType = Material.RED_WOOL;
                    }
                    default -> {
                        color = Color.fromRGB(180, 180, 180);
                        woolType = Material.WHITE_WOOL;
                    }
                }
                Particle.DustOptions dust = new Particle.DustOptions(color, 1.0f);
                double time = System.currentTimeMillis() / 400.0;
                double bob = Math.sin(time) * 0.12;
                Location hologramLoc = baseLoc.clone().add(0, bob, 0);
                String hologramName = "ctf_flag_hologram_" + team.name() + "_" +
                        Math.round(baseLoc.getX()) + "_" + Math.round(baseLoc.getZ());

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (!flag.isPlaced() || flag.getLocation() == null) return;
                    for (Location loc : ring) {
                        loc.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);
                    }
                    ArmorStand existing = null;
                    for (Entity e : hologramLoc.getWorld().getNearbyEntities(hologramLoc, 1.0, 1.0, 1.0)) {
                        if (e instanceof ArmorStand as &&
                                hologramName.equals(as.getCustomName())) {
                            existing = as;
                            break;
                        }
                    }
                    if (existing != null && !existing.isDead()) {
                        existing.teleport(hologramLoc);
                        ItemStack helmet = existing.getEquipment().getHelmet();
                        if (helmet == null || helmet.getType() != woolType) {
                            existing.setHelmet(new ItemStack(woolType));
                        }
                    } else {
                        ArmorStand as = (ArmorStand) hologramLoc.getWorld()
                                .spawnEntity(hologramLoc, EntityType.ARMOR_STAND, false);
                        as.setGravity(false);
                        as.setVisible(false);
                        as.setMarker(true);
                        as.setCustomName(hologramName);
                        as.setCustomNameVisible(false);
                        as.setHelmet(new ItemStack(woolType));
                        as.setSilent(true);
                        as.setInvulnerable(true);
                    }
                });
            });
        }
    }

    private final Map<Team, Map<UUID, Integer>> votes = new HashMap<>();

    public void start() {
        if (status == GameStatus.STARTED) return;
        this.setStatus(GameStatus.STARTED);
        this.assignTeams();

        votes.put(BLUE, new HashMap<>());
        votes.put(RED, new HashMap<>());

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
        for (Team team : List.of(BLUE, RED)) {
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
                    CtfFlag.giveToPlayer(CtfPlayer.get(captain));
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
                player.setTeam(BLUE);
                blueCount++;
            } else {
                player.setTeam(RED);
                redCount++;
            }
        }
    }
}
