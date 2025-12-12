package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CtfServer {
    int gameTime = 0;

    boolean waiting = true;
    int waitTime = 0;

    public CtfServer() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> Main.getInstance().getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1L));
    }

    public void tick() {
        // Check if the game is running
        if (!this.isGameRunning()) {
            waitTime++;

            // Check if enough players are online
            int count = Bukkit.getOnlinePlayers().size();
            if (count < 2) return;

            // If the server has been waiting for one minute, start the game
            if (waiting && waitTime >= 1200) {
                waiting = false;
                CtfGame game = new CtfGame();
                Main.getInstance().setGame(game);
                return;
            }
            return;
        }

        CtfGame game = Main.getInstance().getGame();

        // Check if a team has 5 flags captured
        for (Map.Entry<Team, Integer> entry : game.getPoints().entrySet()) {
            int score = entry.getValue();

            if (score >= 5) {
                this.stopGame(game);
                return;
            }
        }

        // Check the number of players online
        int count = Bukkit.getOnlinePlayers().size();
        if (count == 0) {
            this.stopGame(game);
            return;
        }

        // Check if the gametime is over 15 minuten
        if (gameTime >= 18000) {
            this.stopGame(game);
            return;
        }

        // Add 1 to the times
        gameTime++;
    }

    private boolean isGameRunning() {
        return Main.getInstance().getGame() != null && waiting;
    }

    private void stopGame(CtfGame game) {
        game.stop();
        Main.getInstance().setGame(null);
        waiting = true;
    }
}
