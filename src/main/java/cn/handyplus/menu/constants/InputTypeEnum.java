package cn.handyplus.menu.constants;

import cn.handyplus.lib.core.PatternUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * input节点
 *
 * @author handy
 * @since 1.3.7
 */
@Getter
@AllArgsConstructor
public enum InputTypeEnum {

    /**
     * 全部节点
     */
    TEXT("[text]"),
    NUMBER("[number]"),
    ;

    private final String type;

    public static InputTypeEnum contains(String input) {
        for (InputTypeEnum commandTypeEnum : InputTypeEnum.values()) {
            if (PatternUtil.contains(input, commandTypeEnum.getType())) {
                return commandTypeEnum;
            }
        }
        return TEXT;
    }

    public static String replaceFirst(String input, InputTypeEnum inputTypeEnum) {
        return PatternUtil.replaceFirst(input, inputTypeEnum.getType());
    }

}
