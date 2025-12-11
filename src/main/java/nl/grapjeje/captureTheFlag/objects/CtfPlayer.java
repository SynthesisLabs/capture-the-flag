package nl.grapjeje.captureTheFlag.objects;

import com.craftmend.storm.api.enums.Where;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.enums.Kit;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.models.PlayerModel;
import nl.grapjeje.core.Main;
import nl.grapjeje.core.StormDatabase;
import nl.grapjeje.core.exeptions.PlayerNotFoundException;
import nl.grapjeje.core.registry.AutoRegistry;
import nl.grapjeje.core.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AutoRegistry
public class CtfPlayer {
    @Getter
    private static final Map<UUID, PlayerModel> playerCache = new ConcurrentHashMap<>();

    private final UUID uuid;
    @Getter(AccessLevel.PRIVATE)
    private final PlayerModel model;

    @Setter
    private int kills;
    @Setter
    private int deaths;

    @Setter
    private double coins;

    // Game settings
    @Setter
    private boolean captain = false;
    @Setter
    private boolean hasFlag = false;
    @Setter
    private boolean isDeath = false;

    @Getter
    private CtfScoreboard scoreboard;

    @Setter
    private Team team;
    @Setter
    private org.bukkit.scoreboard.Team scoreboardTeam;
    @Setter
    private Kit kit;

    CtfPlayer(UUID uuid, PlayerModel model) {
        this.uuid = uuid;
        this.model = model;
        this.setTeam(Team.NONE);

        this.scoreboard = CtfScoreboard.get(uuid);
    }

    public static CtfPlayer get(UUID uuid, PlayerModel model) {
        return Registry.get(
                CtfPlayer.class,
                uuid.toString(),
                (args) -> new CtfPlayer((UUID) args[0], (PlayerModel) args[1]),
                uuid, model
        );
    }

    public Player getPlayer() throws PlayerNotFoundException {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) throw new PlayerNotFoundException(uuid);
        return player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public CompletableFuture<Void> save() {
        playerCache.put(uuid, model);
        return CompletableFuture.runAsync(() ->
                StormDatabase.getInstance().saveStormModel(model));
    }

    /**
     * Increases the kill count by one and saves the player to the database.
     * @return the updated amount of kills
     */
    public int addKill() {
        this.kills++;
        return this.getKills();
    }

    /**
     * Increases the death count by one and saves the player to the database.
     * @return the updated amount of deaths
     */
    public int addDeath() {
        this.deaths++;
        return this.getDeaths();
    }

    /**
     * Adds coins to the player's balance and saves the player to the database.
     * @param coins the amount of coins to add
     * @return the updated amount of coins
     */
    public double addCoins(double coins) {
        this.coins += coins;
        return this.getCoins();
    }

    public static CompletableFuture<PlayerModel> loadOrCreatePlayerModelAsync(Player player) {
        PlayerModel cached = playerCache.get(player.getUniqueId());
        if (cached != null) return CompletableFuture.completedFuture(cached);

        return CompletableFuture.supplyAsync(() -> {
            Optional<PlayerModel> playerModelOpt;
            try {
                playerModelOpt = StormDatabase.getInstance().getStorm()
                        .buildQuery(PlayerModel.class)
                        .where("player_uuid", Where.EQUAL, player.getUniqueId().toString())
                        .limit(1)
                        .execute()
                        .join()
                        .stream()
                        .findFirst();
            } catch (Exception ex) {
                ex.printStackTrace();
                Bukkit.getScheduler().runTask(Main.getInstance().getPlugin(), () ->
                        player.sendMessage(nl.grapjeje.core.text.MessageUtil.filterMessage("<warning>⚠ Er is een fout opgetreden bij het ophalen van jouw spelersdata!"))
                );
                return PlayerModel.createNew(player);
            }

            player.getScoreboard();

            PlayerModel model = playerModelOpt.orElseGet(() -> {
                Main.getInstance().getPlugin().getLogger().info("New player grind model made for " + player.getName());
                return PlayerModel.createNew(player);
            });

            playerCache.put(player.getUniqueId(), model);
            return model;
        });
    }
}
