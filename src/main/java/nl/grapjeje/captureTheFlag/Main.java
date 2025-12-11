package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.models.PlayerModel;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.core.Framework;
import nl.grapjeje.core.StormDatabase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Main extends JavaPlugin {

    @Getter
    private static Framework framework;
    @Getter
    private static Main instance;
    @Getter
    private BukkitScheduler scheduler;
    @Getter
    @Setter
    private CtfGame game;

    @Override
    public void onEnable() {
        instance = this;
        framework = Framework.init(this);
        scheduler = this.getServer().getScheduler();

        // Register listeners
        new ListenerManager().init();
        // Register commands
        new CommandManager().init();

        // Register Storm
        framework.initializeStorm(StormDatabase.getInstance().getStorm());
        this.registerModels();

        // Start game server loop
        new CtfServer();
    }

    // All the storm models that needs to be registered
    private void registerModels() {
        framework.registerStormModel(PlayerModel::new);
    }

    @Override
    public void onDisable() {
        if (this.getGame() != null) this.getGame().stop();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand as && as.getCustomName() != null
                        && as.getCustomName().startsWith("ctf_flag_hologram_")) {
                    as.remove();
                }
            }
        }
        if (this.getGame() != null) {
            this.getGame().getPlayers().forEach(ctfPlayer -> {
                if (ctfPlayer.getPlayer() != null) {
                    ctfPlayer.getScoreboard().remove(ctfPlayer.getPlayer());
                }
            });
        }

        if (!StormDatabase.getInstance().isUsingExternalStorm() && nl.grapjeje.core.Main.getDb() != null)
            nl.grapjeje.core.Main.getDb().close();
    }

    public void disablePlugin() {
        this.getServer().getPluginManager().disablePlugin(this);
    }
}
