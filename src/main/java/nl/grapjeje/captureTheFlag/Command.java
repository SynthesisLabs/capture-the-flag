package nl.grapjeje.captureTheFlag;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Command implements CommandExecutor {
    @Getter
    protected Player player;

    @Getter
    private String name;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CommandSender sender;

    public Command(@NotNull String name) {
        this.name = name.toLowerCase();
    }

    public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        this.setSender(commandSender);
        if (this.isPlayer()) this.player = (Player) this.sender;

        this.execute(strings);
        return true;
    }

    protected abstract void execute(String[] args);

    protected boolean isPlayer() {
        return this.sender instanceof Player;
    }

    protected CtfPlayer getCtfPlayer() {
        return CtfPlayer.get(this.player);
    }
}
