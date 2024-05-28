package cn.handyplus.menu.constants;

import cn.handyplus.lib.core.PatternUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点
 *
 * @author handy
 * @since 1.3.1
 */
@Getter
@AllArgsConstructor
public enum CommandTypeEnum {

    /**
     * 全部节点
     */
    MESSAGE("[message]"),
    ALL_MESSAGE("[allMessage]"),
    TITLE("[title]"),
    ALL_TITLE("[allTitle]"),
    ACTIONBAR("[actionbar]"),
    ALL_ACTIONBAR("[allActionbar]"),
    COMMAND("[command]"),
    OP("[op]"),
    CONSOLE("[console]"),
    CLOSE("[close]"),
    SERVER("[server]"),
    OPEN("[open]"),
    REFRESH("[refresh]"),
    ;

    private final String type;

    public static CommandTypeEnum contains(String input) {
        for (CommandTypeEnum commandTypeEnum : CommandTypeEnum.values()) {
            if (PatternUtil.contains(input, commandTypeEnum.getType())) {
                return commandTypeEnum;
            }
        }
        return null;
    }

    public static String replaceFirst(String input, CommandTypeEnum commandTypeEnum) {
        return PatternUtil.replaceFirst(input, commandTypeEnum.getType());
    }

}
