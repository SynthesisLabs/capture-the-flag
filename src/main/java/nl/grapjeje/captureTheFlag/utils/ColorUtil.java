package nl.grapjeje.captureTheFlag.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ColorUtil {

    PRIMARY("#F17F29"),
    SECONDARY("#F28F3B"),
    WARNING("#D91E36"),
    RED("#D91E36"),
    DARK_RED("#A11228"),
    BLUE("#0077CC"),
    DARK_BLUE("#005999"),
    GREEN("#04E762"),
    DARK_GREEN("#038C46"),
    ORANGE("#FF7700"),
    DARK_ORANGE("#CC5C00"),
    PINK("#FF007F"),
    DARK_PINK("#CC0066"),
    PURPLE("#800080"),
    DARK_PURPLE("#5A005A"),
    YELLOW("#FFD700"),
    DARK_YELLOW("#B38F00"),
    GRAY("#A7AAA4"),
    DARK_GRAY("#7D807B"),
    WHITE("#CEEAF7"),
    BLACK("#373F47"),
    BRONZE("#CD7F32"),
    SILVER("#C0C0C0"),
    GOLD("#FFD700");

    private String color;
}
