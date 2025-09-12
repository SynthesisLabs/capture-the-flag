package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static DB db;
    @Getter
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        // Config
        this.saveDefaultConfig();
        config = getConfig();

        // Register database
        db = new DB();
    }

    @Override
    public void onDisable() {
        db.close();
    }
}
