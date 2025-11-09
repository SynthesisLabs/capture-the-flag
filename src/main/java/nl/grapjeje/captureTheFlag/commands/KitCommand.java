package nl.grapjeje.captureTheFlag.commands;

import nl.grapjeje.captureTheFlag.objects.CtfKit;
import nl.grapjeje.captureTheFlag.objects.CtfPlayer;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;
import nl.grapjeje.core.command.Command;
import nl.grapjeje.core.command.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KitCommand implements Command {

    @Override
    public String getName() {
        return "kit";
    }

    @Override
    public void execute(CommandSourceStack source, String[] strings) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Dit command kan alleen door een speler uitgevoerd worden.");
            return;
        }

        CtfPlayer.loadOrCreatePlayerModelAsync(player)
                .thenAcceptAsync(model -> {
                    CtfPlayer ctfPlayer = CtfPlayer.get(player.getUniqueId(), model);
                    if (!ctfPlayer.isDeath()) {
                        ctfPlayer.getPlayer().sendMessage(MessageUtil.filterMessage("<warning>⚠ This command can not be executed now!"));
                        return;
                    }

                    CtfKit kit = CtfKit.get(ctfPlayer);
                    kit.open();
                });
    }
}
