package nl.grapjeje.captureTheFlag.enums;

import lombok.Getter;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;

@Getter
public enum Team {
    BLUE("<blue>"),
    RED("<red>"),
    NONE("<gray>");

    private final String colorCode;

    Team(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return MessageUtil.filterMessageString(colorCode + this.name());
    }
}
