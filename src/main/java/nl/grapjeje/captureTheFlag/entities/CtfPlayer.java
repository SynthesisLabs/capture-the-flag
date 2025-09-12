package nl.grapjeje.captureTheFlag.entities;

import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.enums.Kit;
import nl.grapjeje.captureTheFlag.enums.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CtfPlayer {
    private final static List<CtfPlayer> players = new ArrayList<>();

    private Player player;
    @Setter
    private Team team;
    @Setter
    private Kit kit;

    @Setter
    private int kills;
    @Setter
    private int deaths;

    @Setter
    private double coins;

    CtfPlayer(Player player) {
        this.player = player;
        this.setTeam(Team.NONE);

        players.add(this);
    }

    public static CtfPlayer get(Player player) {
        return players.stream()
                .filter(sp -> sp.getPlayer().equals(player))
                .findFirst()
                .orElse(new CtfPlayer(player));
    }

    // Returns the updated amount of kills
    public int addKill() {
        this.kills++;
        return this.getKills();
    }

    // Returns the updated amount of deaths
    public int addDeath() {
        this.deaths++;
        return this.getDeaths();
    }

    // Returns the current amount of coins
    public double addCoins(double Coins) {
        this.coins += Coins;
        return this.getCoins();
    }
}
