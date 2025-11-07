package nl.grapjeje.captureTheFlag.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {
    public static Component filterMessage(String message) {
        return MiniMessage.miniMessage().deserialize(filterMessageString(message));
    }

    public static String filterMessageStringLegacy(String message) {
        return LegacyComponentSerializer.legacySection().serialize(
                MiniMessage.miniMessage().deserialize(filterMessageString(message))
        );
    }

    public static String filterMessageString(String message) {
        message = message.replace("<primary>", "<color:" + ColorUtil.PRIMARY.getColor() + ">");
        message = message.replace("<secondary>", "<color:" + ColorUtil.SECONDARY.getColor() + ">");
        message = message.replace("<warning>", "<color:" + ColorUtil.WARNING.getColor() + ">");
        message = message.replace("<red>", "<color:" + ColorUtil.RED.getColor() + ">");
        message = message.replace("<dark_red>", "<color:" + ColorUtil.DARK_RED.getColor() + ">");
        message = message.replace("<blue>", "<color:" + ColorUtil.BLUE.getColor() + ">");
        message = message.replace("<dark_blue>", "<color:" + ColorUtil.DARK_BLUE.getColor() + ">");
        message = message.replace("<green>", "<color:" + ColorUtil.GREEN.getColor() + ">");
        message = message.replace("<dark_green>", "<color:" + ColorUtil.DARK_GREEN.getColor() + ">");
        message = message.replace("<orange>", "<color:" + ColorUtil.ORANGE.getColor() + ">");
        message = message.replace("<dark_orange>", "<color:" + ColorUtil.DARK_ORANGE.getColor() + ">");
        message = message.replace("<pink>", "<color:" + ColorUtil.PINK.getColor() + ">");
        message = message.replace("<dark_pink>", "<color:" + ColorUtil.DARK_PINK.getColor() + ">");
        message = message.replace("<purple>", "<color:" + ColorUtil.PURPLE.getColor() + ">");
        message = message.replace("<dark_purple>", "<color:" + ColorUtil.DARK_PURPLE.getColor() + ">");
        message = message.replace("<yellow>", "<color:" + ColorUtil.YELLOW.getColor() + ">");
        message = message.replace("<dark_yellow>", "<color:" + ColorUtil.DARK_YELLOW.getColor() + ">");
        message = message.replace("<gray>", "<color:" + ColorUtil.GRAY.getColor() + ">");
        message = message.replace("<dark_gray>", "<color:" + ColorUtil.DARK_GRAY.getColor() + ">");
        message = message.replace("<white>", "<color:" + ColorUtil.WHITE.getColor() + ">");
        message = message.replace("<black>", "<color:" + ColorUtil.BLACK.getColor() + ">");
        message = message.replace("<bronze>", "<color:" + ColorUtil.BRONZE.getColor() + ">");
        message = message.replace("<silver>", "<color:" + ColorUtil.SILVER.getColor() + ">");
        message = message.replace("<gold>", "<color:" + ColorUtil.GOLD.getColor() + ">");
        return message;
    }
}
