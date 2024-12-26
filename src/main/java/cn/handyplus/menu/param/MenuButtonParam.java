package cn.handyplus.menu.param;

import lombok.Data;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

/**
 * 菜单属性
 *
 * @author handy
 */
@Data
public class MenuButtonParam {

    /**
     * 命令
     */
    private List<String> commands;

    /**
     * 金币
     */
    private int money;

    /**
     * 点券
     */
    private int point;

    /**
     * 物品id
     */
    private Integer id;

    /**
     * 添加附魔
     */
    private Boolean isEnchant;

    /**
     * 隐藏属性效果
     */
    private Boolean hideFlag;

    /**
     * 隐藏附魔效果
     */
    private Boolean hideEnchant;

    /**
     * 限制点击次数
     */
    private int limit;

    /**
     * 限制点击次数并在没有次数后隐藏按钮
     *
     * @since 1.3.4
     */
    private int limitHide;

    /**
     * 限制点击时间 (秒)
     */
    private int cd;

    /**
     * 限制点击时间 (秒) CD中隐藏按钮
     *
     * @since 1.3.4
     */
    private int cdHide;

    /**
     * 自定义点击条件
     */
    private List<String> conditions;

    /**
     * 点击播放声音
     */
    private String sound;

    /**
     * 条件不满足播放的声音
     */
    private String failSound;

    /**
     * 名称
     */
    private String name;
    /**
     * lore
     */
    private List<String> loreList;
    /**
     * 自定义模型
     */
    private int customModelDataId;
    /**
     * 材质
     */
    private String material;
    /**
     * 坐标
     */
    private List<Integer> indexList;

    /**
     * 头颅材质
     *
     * @since 1.1.8
     */
    private String head;

    /**
     * 头颅材质
     *
     * @since 1.2.8
     */
    private String headBase;

    /**
     * 优先级
     *
     * @since 1.2.8
     */
    private Integer priority;

    /**
     * 显示按钮的权限
     *
     * @since 1.1.9
     */
    private String permission;

    /**
     * 商店类型 sell 出售 buy 收购
     *
     * @since 1.2.0
     */
    private String shopType;

    /**
     * 商店物品 格式 物品材质:数量
     *
     * @since 1.2.0
     */
    private String shopMaterial;

    /**
     * 商店金币价格
     *
     * @since 1.2.0
     */
    private String shopMoney;

    /**
     * 商店金币价格
     *
     * @since 1.2.0
     */
    private String shopPoint;

    /**
     * 商店多经济价格
     *
     * @since 1.4.2
     */
    private String shopCurrency;

    /**
     * 显示数量
     *
     * @since 1.2.4
     */
    private int amount;

    /**
     * 动态显示数量
     *
     * @since 1.2.4
     */
    private String dynamicAmount;

    /**
     * 没有对应权限才能显示按钮
     *
     * @since 1.2.4
     */
    private String notPermission;

    /**
     * 输入信息
     *
     * @since 1.3.7
     */
    private String input;

    /**
     * 动作
     *
     * @since 1.5.0
     */
    private Map<String, List<String>> actions;

    /**
     * 点击类型
     *
     * @since 1.5.0
     */
    private String clickType;

    /**
     * 事件当前点击类型
     *
     * @since 1.5.0
     */
    private ClickType eventClickType;
}
