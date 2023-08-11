package cn.handyplus.menu.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 菜单点击限制
 *
 * @author handy
 */
@Getter
@Setter
@TableName(value = "menu_limit", comment = "菜单点击限制")
public class MenuLimit {

    @TableField(value = "id", comment = "ID")
    private Integer id;

    @TableField(value = "player_name", comment = "玩家名称", notNull = true)
    private String playerName;

    @TableField(value = "player_uuid", comment = "玩家uuid", notNull = true)
    private String playerUuid;

    @TableField(value = "menu_item_id", comment = "菜单id", notNull = true)
    private Integer menuItemId;

    @TableField(value = "number", comment = "点击次数", notNull = true)
    private Integer number;

    @TableField(value = "click_time", comment = "点击时间")
    private Date clickTime;

}