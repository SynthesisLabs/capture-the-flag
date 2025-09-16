package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.commands.vanilla.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;

public class CommandManager {

    public void init() {
        // Vanilla commands
        this.registerCommand(new McHelpCommand());
        this.registerCommand(new McMeCommand());
        this.registerCommand(new McTeamMsgCommand());
        this.registerCommand(new McTellCommand());
        this.registerCommand(new McTmCommand());
        this.registerCommand(new McTriggerCommand());
        this.registerCommand(new McWCommand());
    }

    private void registerCommand(nl.grapjeje.captureTheFlag.Command command) {
        PluginCommand bukkitCommand = Main.getInstance().getCommand(command.getName());
        if (bukkitCommand != null) {
            try {
                PluginManager pluginManager = Bukkit.getPluginManager();
                Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) commandMapField.get(pluginManager);

                Command existingCommand = commandMap.getCommand(command.getName());
                if (existingCommand != null) existingCommand.unregister(commandMap);
                commandMap.register(Main.getInstance().getName(), bukkitCommand);

                bukkitCommand.setExecutor(command);
            } catch (Exception e) {
                e.printStackTrace();
                Main.getInstance().disablePlugin();
            }
        }
    }
}
