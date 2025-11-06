package nl.grapjeje.captureTheFlag.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "player")
public class PlayerModel extends StormModel {

    @Column(name = "player_uuid")
    private UUID playerUuid;

    @Column(name = "kills", defaultValue = "0")
    private Integer kills = 0;

    @Column(name = "death", defaultValue = "0")
    private Integer death = 0;

    @Column(name = "coins", defaultValue = "0")
    private Double coins = 0.0;

    public static PlayerModel createNew(Player player) {
        PlayerModel m = new PlayerModel();
        m.setPlayerUuid(player.getUniqueId());
        m.setKills(0);
        m.setDeath(0);
        m.setCoins(0.0);
        return m;
    }
}
