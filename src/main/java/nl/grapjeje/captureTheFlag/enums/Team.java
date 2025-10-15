package nl.grapjeje.captureTheFlag.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.grapjeje.captureTheFlag.utils.MessageUtil;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Team {
    BLUE("<blue>"),
    RED("<red>"),
    NONE("<gray>");

    private final String colorCode;

    @Override
    public String toString() {
        return MessageUtil.filterMessageString(colorCode + this.name());
    }
}
