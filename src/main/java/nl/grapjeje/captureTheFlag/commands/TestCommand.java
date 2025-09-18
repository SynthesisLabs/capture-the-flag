package nl.grapjeje.captureTheFlag.commands;

import nl.grapjeje.captureTheFlag.Command;
import nl.grapjeje.captureTheFlag.Main;
import nl.grapjeje.captureTheFlag.objects.CtfGame;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;

public class TestCommand extends Command {
    public TestCommand() {
        super("test");
    }

    @Override
    protected void execute(String[] args) {
        CtfGame game = new CtfGame();
        Main.getInstance().setGame(game);
    }
}
