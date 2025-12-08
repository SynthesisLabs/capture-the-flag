package nl.grapjeje.captureTheFlag.objects;

import fr.skytasul.glowingentities.GlowingEntities;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.enums.GameStatus;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import nl.grapjeje.core.GlowUtil;
import nl.grapjeje.core.gui.Gui;
import nl.grapjeje.core.gui.GuiButton;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static nl.grapjeje.captureTheFlag.enums.Team.BLUE;
import static nl.grapjeje.captureTheFlag.enums.Team.RED;

@Getter
public class CtfGame {
    private final List<CtfPlayer> players = new ArrayList<>();
    @Setter
    private GameStatus status = GameStatus.STOPPED;
    private final Map<Team, CtfFlag> gameFlags = new HashMap<>();
    private final double flagRadius = 2;
    private final Map<Team, Integer> points = new HashMap<>();
    private final Map<Team, DroppedFlag> droppedFlags = new HashMap<>();
    private final Map<UUID, Long> captureProgress = new HashMap<>();
    private final Map<Team, Map<UUID, Integer>> votes = new HashMap<>();

    public CtfGame() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            CompletableFuture<Void> future = CtfPlayer.loadOrCreatePlayerModelAsync(p)
                    .thenAccept(model -> players.add(CtfPlayer.get(p.getUniqueId(), model)))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> Main.getInstance().getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1));
    }

    // ===== Game Lifecycle =====
    public void start() {
        if (status == GameStatus.STARTED) return;
        this.setStatus(GameStatus.STARTED);
        this.assignTeams();
        votes.put(BLUE, new HashMap<>());
        votes.put(RED, new HashMap<>());
        for (CtfPlayer ctfPlayer : this.players) {
            ctfPlayer.getScoreboard().create(ctfPlayer.getPlayer(), this);
            this.openVoteMenu(ctfPlayer);
        }
        Main.getInstance().getScheduler().runTaskLater(Main.getInstance(), this::pickCaptains, 20 * 15);
    }

    public void stop() {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream()
                .filter(team -> team.getName().equalsIgnoreCase("RED") || team.getName().equalsIgnoreCase("BLUE"))
                .forEach(org.bukkit.scoreboard.Team::unregister);

//        for (CtfPlayer ctfPlayer : this.players) {
//            ctfPlayer.getScoreboard().remove(ctfPlayer.getPlayer(), this);
//        }
    }

    private void tick() {
        this.start();
        this.handleAllDroppedFlags();
        this.showFlag();

        final long captureTime = 3000;
        final double radius = 3.0;

        for (CtfPlayer ctfPlayer : players) {
            Player player = ctfPlayer.getPlayer();
            UUID uuid = player.getUniqueId();

            // Add name color
            Component coloredName = MessageUtil.filterMessage(ctfPlayer.getTeam().getColorCode() + player.getName());
            player.displayName(coloredName);
            player.playerListName(coloredName);

            // Glow effect
            try {
                GlowingEntities glowingEntities = GlowUtil.getInstance().getGlowingEntities();
                if (ctfPlayer.isHasFlag()) {
                    ChatColor glowColor = ctfPlayer.getTeam() == RED ? ChatColor.RED : ChatColor.BLUE;
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        glowingEntities.setGlowing(player, onlinePlayer, glowColor);
                    }
                } else {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        glowingEntities.unsetGlowing(player, onlinePlayer);
                    }
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }

            if (!ctfPlayer.isDeath() && player.getGameMode() != GameMode.SPECTATOR) {
                Location playerLoc = player.getLocation();
                gameFlags.forEach((team, flag) -> {
                    Location flagLoc = flag.getLocation();
                    if (flagLoc == null || !playerLoc.getWorld().equals(flagLoc.getWorld())) return;

                    // Steal enemy flag
                    if (ctfPlayer.getTeam() != team && !ctfPlayer.isHasFlag() && !flag.isStolen()) {
                        if (playerLoc.distance(flagLoc) <= radius) {
                            long now = System.currentTimeMillis();
                            captureProgress.putIfAbsent(uuid, now);
                            long elapsed = now - captureProgress.get(uuid);

                            double progress = Math.min(1.0, elapsed / (double) captureTime);
                            int totalBars = 20;
                            int filledBars = (int) (progress * totalBars);
                            StringBuilder bar = new StringBuilder();
                            for (int i = 0; i < totalBars; i++) bar.append(i < filledBars ? "<primary>|" : "<gray>|");
                            player.sendActionBar(MessageUtil.filterMessage(bar.toString()));

                            if (elapsed >= captureTime) {
                                captureProgress.remove(uuid);
                                ctfPlayer.setHasFlag(true);
                                flag.setStolen(true);

                                if (flagLoc.getBlock().getType() == Material.BLUE_WOOL || flagLoc.getBlock().getType() == Material.RED_WOOL)
                                    flagLoc.getBlock().setType(Material.AIR);

                                player.sendActionBar(MessageUtil.filterMessage("<primary>You captured the flag!"));
                            }
                        } else if (captureProgress.containsKey(uuid)) {
                            captureProgress.remove(uuid);
                            player.sendActionBar(MessageUtil.filterMessage("<gray>Capture cancelled"));
                        }
                    }

                    // Return own flag / score
                    if (ctfPlayer.isHasFlag() && ctfPlayer.getTeam() == team) {
                        if (playerLoc.distance(flagLoc) <= radius) {
                            long now = System.currentTimeMillis();
                            captureProgress.putIfAbsent(uuid, now);
                            long elapsed = now - captureProgress.get(uuid);

                            double progress = Math.min(1.0, elapsed / (double) captureTime);
                            int totalBars = 20;
                            int filledBars = (int) (progress * totalBars);
                            StringBuilder bar = new StringBuilder();
                            for (int i = 0; i < totalBars; i++) bar.append(i < filledBars ? "<primary>|" : "<gray>|");
                            player.sendActionBar(MessageUtil.filterMessage(bar.toString()));

                            if (elapsed >= captureTime) {
                                captureProgress.remove(uuid);
                                ctfPlayer.setHasFlag(false);
                                flag.setStolen(false);
                                flag.setDropped(false);
                                player.sendActionBar(MessageUtil.filterMessage("<primary>You returned the flag!"));

                                int teamPoints = points.getOrDefault(ctfPlayer.getTeam(), 0) + 1;
                                points.put(ctfPlayer.getTeam(), teamPoints);

                                Bukkit.broadcast(MessageUtil.filterMessage(
                                        "<gray><bold>🏁 <!bold>Team " + ctfPlayer.getTeam() +
                                                "<gray> scored! (" +
                                                "<primary>" + points.getOrDefault(BLUE, 0) +
                                                "<gray> - <primary>" + points.getOrDefault(RED, 0) +
                                                "<gray>)"
                                ));

                                this.respawnFlagHologram(flag, team);
                                droppedFlags.remove(team);
                            }
                        } else if (captureProgress.containsKey(uuid)) {
                            captureProgress.remove(uuid);
                            player.sendActionBar(MessageUtil.filterMessage("<gray>Return cancelled"));
                        }
                    }
                });
                this.handleDroppedFlags(player, ctfPlayer);
            }
        }
    }

    // ===== Team Management =====
    private void assignTeams() {
        int blueCount = 0, redCount = 0;
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
                    CtfPlayer.loadOrCreatePlayerModelAsync(captain)
                            .thenAccept(model -> {
                                CtfPlayer.get(captain.getUniqueId(), model).setCaptain(true);
                                for (CtfPlayer p : this.players) {
                                    if (p.getTeam() == Team.NONE) continue;
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () ->
                                            p.getPlayer().sendMessage(MessageUtil.filterMessage(
                                                    "<primary><bold>🏆 " + captain.getName() +
                                                            "<!bold> has been chosen as captain for team <bold>" + team
                                            )));
                                }
                                Bukkit.getScheduler().runTask(Main.getInstance(), () ->
                                        captain.sendMessage(
                                                MessageUtil.filterMessage("<gray><bold>🎖 <!bold>You are now the captain of team <bold>" + team + "!")
                                        ));
                                CtfFlag.giveToPlayer(CtfPlayer.get(captain.getUniqueId(), model));
                            }).exceptionally(ex -> {
                                ex.printStackTrace();
                                return null;
                            });
                }
            }
        }
    }

    // ===== Voting Menu =====
    private void openVoteMenu(CtfPlayer ctfPlayer) {
        Gui.Builder builder = Gui.builder(InventoryType.CHEST, Component.text("Vote your captain"));
        builder.withSize(27);

        Player player = ctfPlayer.getPlayer();
        List<Player> teamMates = this.players.stream()
                .filter(p -> p.getTeam() == ctfPlayer.getTeam())
                .map(CtfPlayer::getPlayer).toList();

        for (int i = 0; i < teamMates.size(); i++) {
            Player mate = teamMates.get(i);
            final UUID mateId = mate.getUniqueId();
            final String mateName = mate.getName();

            GuiButton button = GuiButton.builder()
                    .withMaterial(Material.PLAYER_HEAD)
                    .withName(MessageUtil.filterMessage("<primary>" + mateName))
                    .withLore(MessageUtil.filterMessage("<gray>Click to vote for your team captain"))
                    .withClickEvent((g, p, c) -> {
                        Main.getInstance().getGame().addVote(ctfPlayer.getTeam(), mateId);
                        p.closeInventory();
                        p.sendMessage(MessageUtil.filterMessage("<gray>You voted for <primary>" + mateName));
                    })
                    .build();

            builder.withButton(i, button);
        }
        builder.withFiller(GuiButton.getFiller());
        Gui gui = builder.build();
        gui.register();
        gui.open(player.getPlayer());
    }

    // ===== Flag Display & Holograms =====
    private void showFlag() {
        if (gameFlags.isEmpty()) return;
        for (Map.Entry<Team, CtfFlag> entry : gameFlags.entrySet()) {
            CtfFlag flag = entry.getValue();
            Team team = entry.getKey();
            Location baseLoc = flag.getLocation();
            if (baseLoc == null) continue;

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                String hologramPrefix = "ctf_flag_hologram_" + team.name() + "_";
                for (Entity e : baseLoc.getWorld().getNearbyEntities(baseLoc, 5.0, 5.0, 5.0)) {
                    if (e instanceof ArmorStand as && as.getCustomName() != null &&
                            as.getCustomName().startsWith(hologramPrefix)) {
                        if (flag.isStolen() || flag.getLocation() == null || as.getLocation().distance(flag.getLocation()) > 1.5)
                            as.remove();
                    }
                }
            });

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                final int points = 35;
                final double cx = baseLoc.getX();
                final double cy = baseLoc.getY();
                final double cz = baseLoc.getZ();
                List<Location> ring = new ArrayList<>(points);
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = Math.cos(angle) * flagRadius;
                    double z = Math.sin(angle) * flagRadius;
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
                    for (Location loc : ring)
                        loc.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dust);

                    if (!flag.isStolen()) {
                        ArmorStand existing = null;
                        for (Entity e : hologramLoc.getWorld().getNearbyEntities(hologramLoc, 1.0, 1.0, 1.0)) {
                            if (e instanceof ArmorStand as && hologramName.equals(as.getCustomName())) {
                                existing = as;
                                break;
                            }
                        }
                        if (existing != null && !existing.isDead()) {
                            existing.teleport(hologramLoc);
                            ItemStack helmet = existing.getEquipment().getHelmet();
                            if ((helmet == null || helmet.getType() != woolType) && !flag.isStolen())
                                existing.setHelmet(new ItemStack(woolType));
                        } else {
                            ArmorStand as = (ArmorStand) hologramLoc.getWorld()
                                    .spawnEntity(hologramLoc, EntityType.ARMOR_STAND, false);
                            as.setGravity(false);
                            as.setVisible(false);
                            as.setMarker(true);
                            as.setCustomName(hologramName);
                            as.setCustomNameVisible(false);
                            if (!flag.isStolen())
                                as.setHelmet(new ItemStack(woolType));
                            as.setSilent(true);
                            as.setInvulnerable(true);
                        }
                    }
                });
            });
        }
    }

    private void respawnFlagHologram(CtfFlag flag, Team team) {
        Location baseLoc = flag.getLocation();
        if (baseLoc == null) return;

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            String hologramPrefix = "ctf_flag_hologram_" + team.name() + "_";
            for (Entity e : baseLoc.getWorld().getNearbyEntities(baseLoc, 5.0, 5.0, 5.0)) {
                if (e instanceof ArmorStand as && as.getCustomName() != null &&
                        as.getCustomName().startsWith(hologramPrefix)) {
                    as.remove();
                }
            }
        });

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::showFlag, 1L);
    }

    // ===== Dropped Flag Handling =====
    private void handleAllDroppedFlags() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Team, DroppedFlag>> iter = droppedFlags.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Team, DroppedFlag> entry = iter.next();
            DroppedFlag df = entry.getValue();

            int remaining = (int) Math.max(0, 20 - (now - df.dropTime) / 1000);
            if (df.stand != null)
                df.stand.customName(MessageUtil.filterMessage("<primary>Flag returns in " + remaining + "s"));

            if (now - df.dropTime >= 20_000) {
                if (df.stand != null) df.stand.remove();
                gameFlags.get(df.team).setStolen(false);
                gameFlags.get(df.team).setDropped(false);
                iter.remove();
                Bukkit.broadcast(MessageUtil.filterMessage("<gray>The " + df.team + " flag returned to base!"));
                this.respawnFlagHologram(gameFlags.get(df.team), df.team);
                continue;
            }

            if (gameFlags.get(df.team).isDropped())
                this.spawnCircleParticles(df.location, df.team);
        }
    }

    private void handleDroppedFlags(Player player, CtfPlayer ctfPlayer) {
        double radius = 2.0;
        Iterator<Map.Entry<Team, DroppedFlag>> iter = droppedFlags.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Team, DroppedFlag> entry = iter.next();
            DroppedFlag df = entry.getValue();

            if (player.getLocation().distance(df.location) <= radius) {
                if (ctfPlayer.getTeam() == df.team) {
                    if (df.stand != null) df.stand.remove();
                    gameFlags.get(df.team).setStolen(false);
                    gameFlags.get(df.team).setDropped(false);
                    iter.remove();
                    player.sendActionBar(MessageUtil.filterMessage("<primary>You returned your flag!"));
                    this.respawnFlagHologram(gameFlags.get(df.team), df.team);
                } else if (!ctfPlayer.isHasFlag()) {
                    if (df.stand != null) df.stand.remove();
                    ctfPlayer.setHasFlag(true);
                    gameFlags.get(df.team).setStolen(true);
                    gameFlags.get(df.team).setDropped(false);
                    iter.remove();
                    player.sendActionBar(MessageUtil.filterMessage("<primary>You picked up the flag!"));
                    Bukkit.broadcast(MessageUtil.filterMessage("<gray>" + player.getName() + " has stolen the " + df.team + " flag!"));
                }
            }
        }
    }

    private void spawnCircleParticles(Location loc, Team team) {
        int points = 20;
        double radius = 2.0;
        Color color = team == RED ? Color.RED : Color.BLUE;
        Particle.DustOptions dust = new Particle.DustOptions(color, 1.0f);
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            loc.getWorld().spawnParticle(Particle.DUST, loc.clone().add(x, 0.1, z), 1, dust);
        }
    }

    private ArmorStand spawnFlagStand(Location loc, Team team) {
        ArmorStand stand = loc.getWorld().spawn(loc.clone().add(0, 0.2, 0), ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setMarker(true);
            a.setCustomNameVisible(true);
            a.customName(MessageUtil.filterMessage("<primary>Flag here!"));
            a.setGravity(false);
            a.setInvulnerable(true);
        });
        Material wool = team == RED ? Material.RED_WOOL : Material.BLUE_WOOL;
        stand.setHelmet(new ItemStack(wool));
        return stand;
    }

    // ===== Dropped Flag Inner Class =====
    public class DroppedFlag {
        Location location;
        Team team;
        long dropTime;
        ArmorStand stand;

        public DroppedFlag(Location loc, Team team) {
            this.location = loc;
            this.team = team;
            this.dropTime = System.currentTimeMillis();
            this.stand = spawnFlagStand(loc, team);
        }
    }
}