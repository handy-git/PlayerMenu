package cn.handyplus.menu.constants;

import cn.handyplus.menu.util.ConfigUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * tab提醒
 *
 * @author handy
 */
@Getter
@AllArgsConstructor
public enum TabListEnum {
    /**
     * 第一层提醒
     */
    FIRST(Arrays.asList("reload", "open", "getMaterial", "create", "view", "close"), 0, null, 1),

    CREATE_TWO(Arrays.asList("9", "18", "27", "36", "45", "54"), 1, "create", 2),

    OPEN_TWO(new ArrayList<>(ConfigUtil.MENU_CONFIG_MAP.keySet()), 1, "open", 2),
    VIEW_TWO(new ArrayList<>(ConfigUtil.MENU_CONFIG_MAP.keySet()), 1, "view", 2),
    ;

    /**
     * 返回的List
     */
    private final List<String> list;
    /**
     * 识别的上个参数的位置
     */
    private final int befPos;
    /**
     * 上个参数的内容
     */
    private final String bef;
    /**
     * 这个参数可以出现的位置
     */
    private final int num;

    /**
     * 获取提醒
     *
     * @param args       参数
     * @param argsLength 参数长度
     * @return 提醒
     */
    public static List<String> returnList(String[] args, int argsLength) {
        List<String> completions = new ArrayList<>();

        // open和view参数特殊处理
        if (argsLength == 2 && ("open".equalsIgnoreCase(args[0]) || "view".equalsIgnoreCase(args[0]))) {
            return new ArrayList<>(ConfigUtil.MENU_CONFIG_MAP.keySet());
        }

        for (TabListEnum tabListEnum : TabListEnum.values()) {
            if (tabListEnum.getBefPos() - 1 >= args.length) {
                continue;
            }
            if ((tabListEnum.getBef() == null || tabListEnum.getBef().equalsIgnoreCase(args[tabListEnum.getBefPos() - 1])) && tabListEnum.getNum() == argsLength) {
                completions = tabListEnum.getList();
            }
        }
        return completions;
    }

}
