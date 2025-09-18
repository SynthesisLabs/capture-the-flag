package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.DB;
import nl.grapjeje.captureTheFlag.enums.Kit;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.exeptions.PlayerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CtfPlayer {
    private final static List<CtfPlayer> players = new ArrayList<>();

    private final UUID uuid;
    @Setter
    private Team team;
    @Setter
    private Kit kit;

    private int kills;
    private int deaths;

    private double coins;

    // Game settings
    @Setter
    private boolean captain = false;

    CtfPlayer(UUID uuid) {
        this.uuid = uuid;
        this.setTeam(Team.NONE);

        players.add(this);
    }

    public CtfPlayer(UUID uuid, int kills, int deaths, double coins) {
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.coins = coins;
        this.setTeam(Team.NONE);

        players.add(this);
    }

    public static CtfPlayer get(Player player) {
        return players.stream()
                .filter(sp -> sp.getUuid().equals(player.getUniqueId()))
                .findFirst()
                .orElse(new CtfPlayer(player.getUniqueId()));
    }

    public Player getPlayer() throws PlayerNotFoundException {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) throw new PlayerNotFoundException(uuid);
        return player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    /**
     * Increases the kill count by one and saves the player to the database.
     * @return the updated amount of kills
     */
    public int addKill() {
        this.kills++;
        this.savePlayer();
        return this.getKills();
    }

    /**
     * Increases the death count by one and saves the player to the database.
     * @return the updated amount of deaths
     */
    public int addDeath() {
        this.deaths++;
        this.savePlayer();
        return this.getDeaths();
    }

    /**
     * Adds coins to the player's balance and saves the player to the database.
     * @param coins the amount of coins to add
     * @return the updated amount of coins
     */
    public double addCoins(double coins) {
        this.coins += coins;
        this.savePlayer();
        return this.getCoins();
    }

    /**
     * Sets the player's kill count and saves the player to the database.
     * @param kills the new kill count
     * @return the updated amount of kills
     */
    public int setKills(int kills) {
        this.kills = kills;
        this.savePlayer();
        return this.getKills();
    }

    /**
     * Sets the player's death count and saves the player to the database.
     * @param deaths the new death count
     * @return the updated amount of deaths
     */
    public int setDeaths(int deaths) {
        this.deaths = deaths;
        this.savePlayer();
        return this.getDeaths();
    }

    /**
     * Sets the player's coin balance and saves the player to the database.
     * @param coins the new coin balance
     * @return the updated amount of coins
     */
    public double setCoins(double coins) {
        this.coins = coins;
        this.savePlayer();
        return this.getCoins();
    }

    public void savePlayer() {
        String sql = "INSERT INTO ctf_players (uuid, kills, deaths, coins) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "uuid = VALUES(uuid), " +
                "kills = VALUES(kills), deaths = VALUES(deaths), coins = VALUES(coins)";

        try (PreparedStatement stmt = DB.getConnection().prepareStatement(sql)) {
            stmt.setString(1, this.getPlayer().getUniqueId().toString());
            stmt.setInt(2, this.getKills());
            stmt.setInt(3, this.getDeaths());
            stmt.setDouble(4, this.getCoins());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
