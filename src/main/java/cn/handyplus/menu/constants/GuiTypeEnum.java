package cn.handyplus.menu.constants;

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
    CREATE("create", "创建菜单"),
    MENU("menu", "查看菜单"),
    ITEM("item", "物品库"),
    ;

    private final String type;
    private final String title;

}