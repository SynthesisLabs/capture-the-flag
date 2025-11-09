package nl.grapjeje.captureTheFlag;

import nl.grapjeje.captureTheFlag.commands.KitCommand;
import nl.grapjeje.captureTheFlag.commands.PlaceFlagCommand;
import nl.grapjeje.captureTheFlag.commands.TestCommand;
import nl.grapjeje.captureTheFlag.commands.vanilla.*;

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

        Main.getFramework().registerCommand(KitCommand::new);

        // Test command
        Main.getFramework().registerCommand(TestCommand::new);
        Main.getFramework().registerCommand(PlaceFlagCommand::new);
    }
}
