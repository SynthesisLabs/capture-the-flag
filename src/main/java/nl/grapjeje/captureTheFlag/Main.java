package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Main extends JavaPlugin {

    @Getter
    private static DB db;
    @Getter
    private static FileConfiguration fileConfig;
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
        scheduler = this.getServer().getScheduler();

        // Config
        this.saveDefaultConfig();
        fileConfig = this.getConfig();

        // Register database
        db = new DB();

        // Register listeners
        new ListenerManager().init();
        // Register commands
        new CommandManager().init();
    }

    @Override
    public void onDisable() {
        if (db != null) db.close();
    }

    public void disablePlugin() {
        this.getServer().getPluginManager().disablePlugin(this);
    }
}
