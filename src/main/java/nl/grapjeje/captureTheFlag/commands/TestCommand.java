package nl.grapjeje.captureTheFlag.commands;

import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.core.command.Command;
import nl.grapjeje.core.command.CommandSourceStack;

public class TestCommand implements Command {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CtfGame game = new CtfGame();
        Main.getInstance().setGame(game);
    }
}
