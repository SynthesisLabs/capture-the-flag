package nl.grapjeje.captureTheFlag.objects;

import lombok.Getter;
import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import nl.grapjeje.core.registry.AutoRegistry;
import org.bukkit.entity.Player;

@AutoRegistry
@Getter
public class CtfFlagBar {
    private final Player player;
    private final CtfGame game;

    public CtfFlagBar(Player player, CtfGame game) {
        this.player = player;
        this.game = game;
    }

    public void update() {
        if (player == null || game == null) return;

        CtfPlayer blueFlagCarrier = null;
        CtfPlayer redFlagCarrier = null;
        CtfPlayer viewerCtfPlayer = null;

        for (CtfPlayer ctfPlayer : game.getPlayers()) {
            if (ctfPlayer.getPlayer() != null && ctfPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                viewerCtfPlayer = ctfPlayer;
            }
            if (ctfPlayer.isHasFlag()) {
                if (ctfPlayer.getTeam() == Team.BLUE) {
                    blueFlagCarrier = ctfPlayer;
                } else if (ctfPlayer.getTeam() == Team.RED) {
                    redFlagCarrier = ctfPlayer;
                }
            }
        }

        CtfPlayer targetCarrier = null;
        Team targetTeam = null;

        if (viewerCtfPlayer != null) {
            Team viewerTeam = viewerCtfPlayer.getTeam();

            if (blueFlagCarrier != null && redFlagCarrier != null) {
                if (viewerTeam == Team.BLUE) {
                    targetCarrier = redFlagCarrier;
                    targetTeam = Team.RED;
                } else if (viewerTeam == Team.RED) {
                    targetCarrier = blueFlagCarrier;
                    targetTeam = Team.BLUE;
                }
            }
            else if (blueFlagCarrier != null) {
                targetCarrier = blueFlagCarrier;
                targetTeam = Team.BLUE;
            } else if (redFlagCarrier != null) {
                targetCarrier = redFlagCarrier;
                targetTeam = Team.RED;
            }
        }

        if (targetCarrier != null && targetCarrier.getPlayer() != null) {
            String compassBar = createCompassBar(player, targetCarrier.getPlayer(), targetTeam);
            player.sendActionBar(MessageUtil.filterMessage(compassBar));
        }
    }

    public void remove() {
    }

    private String createCompassBar(Player viewer, Player target, Team team) {
        double angle = calculateRelativeAngle(viewer, target);
        int distance = (int) viewer.getLocation().distance(target.getLocation());

        int totalBars = 20;
        int centerIndex = totalBars / 2;

        int targetIndex = (int) ((angle + 180) / 360.0 * totalBars);
        targetIndex = Math.max(0, Math.min(totalBars - 1, targetIndex));

        StringBuilder compass = new StringBuilder();
        String teamColor = team == Team.BLUE ? "<primary>" : "<red>";

        for (int i = 0; i < totalBars; i++) {
            if (i == targetIndex) {
                compass.append(teamColor).append("●");
            } else if (i == centerIndex) {
                compass.append("<white>|");
            } else {
                compass.append("<gray>|");
            }
        }

        compass.append(" ").append(teamColor).append("🚩 ").append(distance).append("m");

        return compass.toString();
    }

    private double calculateRelativeAngle(Player viewer, Player target) {
        float viewerYaw = viewer.getLocation().getYaw();

        double dx = target.getLocation().getX() - viewer.getLocation().getX();
        double dz = target.getLocation().getZ() - viewer.getLocation().getZ();
        double angleToTarget = Math.toDegrees(Math.atan2(dz, dx)) - 90;

        double relativeAngle = angleToTarget - viewerYaw;

        while (relativeAngle > 180) relativeAngle -= 360;
        while (relativeAngle < -180) relativeAngle += 360;

        return relativeAngle;
    }
}
