package cn.handyplus.menu.constants;

import cn.handyplus.lib.db.DbTypeEnum;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.service.MenuLimitService;
import cn.handyplus.menu.util.ConfigUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    FIRST(Arrays.asList("reload", "open", "getMaterial", "create", "view", "close", "adminOpen", "clock", "clear", "addItem", "getItem", "convert"), 0, null, 1),

    CREATE_TWO(Arrays.asList("9", "18", "27", "36", "45", "54"), 1, "create", 2),

    OPEN_TWO(new ArrayList<>(), 1, "open", 2),
    OPEN_THREE(null, 1, "open", 3),
    VIEW_TWO(new ArrayList<>(), 1, "view", 2),

    ADMIN_OPEN_TWO(new ArrayList<>(), 1, "adminOpen", 2),
    ADMIN_OPEN_THREE(null, 1, "adminOpen", 3),
    ADMIN_OPEN_FOUR(null, 1, "adminOpen", 4),

    CLEAR_TWO(new ArrayList<>(), 1, "clear", 2),

    GET_ITEM_TWO(Collections.singletonList(BaseUtil.getLangMsg("tabHelp.id")), 1, "getItem", 2),
    GET_ITEM_THREE(Collections.singletonList(BaseUtil.getLangMsg("tabHelp.number")), 1, "getItem", 3),
    GET_ITEM_FOUR(null, 1, "getItem", 4),

    CONVERT_ONE(DbTypeEnum.getEnum(), 1, "convert", 2),
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
        for (TabListEnum tabListEnum : TabListEnum.values()) {
            // 过滤掉参数长度不满足要求的情况
            if (tabListEnum.getBefPos() - 1 >= args.length) {
                continue;
            }
            // 过滤掉前置参数不匹配的情况
            if (tabListEnum.getBef() != null && !tabListEnum.getBef().equalsIgnoreCase(args[tabListEnum.getBefPos() - 1])) {
                continue;
            }
            // 过滤掉参数长度不匹配的情况
            if (tabListEnum.getNum() != argsLength) {
                continue;
            }
            // 菜单key
            if (tabListEnum.equals(OPEN_TWO) || tabListEnum.equals(ADMIN_OPEN_TWO) || tabListEnum.equals(VIEW_TWO)) {
                return new ArrayList<>(ConfigUtil.MENU_CONFIG_MAP.keySet());
            }
            // 清理数据返回id
            if (tabListEnum.equals(CLEAR_TWO)) {
                return MenuLimitService.getInstance().selectMenuItemIds();
            }
            return tabListEnum.getList();
        }
        return completions;
    }

}
