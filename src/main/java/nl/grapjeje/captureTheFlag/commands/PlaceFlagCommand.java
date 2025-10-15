package nl.grapjeje.captureTheFlag.commands;

import nl.grapjeje.captureTheFlag.enums.Team;
import nl.grapjeje.captureTheFlag.objects.CtfFlag;
import nl.grapjeje.core.command.Command;
import nl.grapjeje.core.command.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PlaceFlagCommand implements Command {

    @Override
    public String getName() {
        return "placeflag";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Dit command kan alleen door een speler uitgevoerd worden.");
            return;
        }
        CtfFlag flag = new CtfFlag();
        if (args.length == 0) return;

        if (args.length == 1) {
            if (Objects.equals(args[0], "red"))
                flag.place(player.getLocation(), Team.RED);
            else if (Objects.equals(args[0], "blue"))
                flag.place(player.getLocation(), Team.BLUE);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, @NotNull String[] args) {
        return List.of("red", "blue");
    }
}
