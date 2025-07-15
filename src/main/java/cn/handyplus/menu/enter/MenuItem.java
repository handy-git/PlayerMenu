package cn.handyplus.menu.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 菜单物品
 *
 * @author handy
 */
@Getter
@Setter
@TableName(value = "menu_item", comment = "菜单物品")
public class MenuItem {

    @TableField(value = "id", comment = "ID")
    private Integer id;

    @TableField(value = "item_stack", comment = "物品", length = 20000, notNull = true)
    private String itemStack;

    @TableField(value = "md5", comment = "md5")
    private String md5;

}
