package nl.grapjeje.captureTheFlag.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {
    public static Component filterMessage(String message) {
        return MiniMessage.miniMessage().deserialize(filterMessageString(message));
    }

    public static String filterMessageString(String message) {
        message = message.replace("<primary>", "<color:" + ColorUtil.PRIMARY.getColor() + ">");
        return nl.grapjeje.core.text.MessageUtil.filterMessageString(message);
    }
}
