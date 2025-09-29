package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.core.Framework;
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
    }

    @Override
    public void onDisable() {

    }

    public void disablePlugin() {
        this.getServer().getPluginManager().disablePlugin(this);
    }
}
