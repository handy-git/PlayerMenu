package cn.handyplus.menu.constants;

import cn.handyplus.lib.util.BaseUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * gui类型
 *
 * @author handy
 */
@Getter
@AllArgsConstructor
public enum GuiTypeEnum {
    /**
     * gui类型
     */
    CREATE("create", BaseUtil.replaceChatColor("&a创建菜单")),
    MENU("menu", BaseUtil.replaceChatColor("&a查看菜单")),
    ;

    private final String type;
    private final String title;

}