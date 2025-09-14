package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static DB db;
    @Getter
    private static FileConfiguration fileConfig;

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        // Config
        this.saveDefaultConfig();
        fileConfig = this.getConfig();

        // Register database
        db = new DB();

        // Register listeners
        new ListenerManager().init();
    }

    @Override
    public void onDisable() {
        if (db != null) db.close();
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }
}
