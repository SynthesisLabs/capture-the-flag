package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.commands.PlaceFlagCommand;
import nl.grapjeje.captureTheFlag.commands.TestCommand;
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
        Main.getFramework().registerCommand(McHelpCommand::new);
        Main.getFramework().registerCommand(McMeCommand::new);
        Main.getFramework().registerCommand(McTeamMsgCommand::new);
        Main.getFramework().registerCommand(McTellCommand::new);
        Main.getFramework().registerCommand(McTmCommand::new);
        Main.getFramework().registerCommand(McTriggerCommand::new);
        Main.getFramework().registerCommand(McWCommand::new);

        // Test command
        Main.getFramework().registerCommand(TestCommand::new);
        Main.getFramework().registerCommand(PlaceFlagCommand::new);
    }
}
