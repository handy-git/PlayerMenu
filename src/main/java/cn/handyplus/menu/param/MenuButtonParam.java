package cn.handyplus.menu.param;

import lombok.Data;

import java.util.List;

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
     * 限制点击时间 (秒)
     */
    private int cd;

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
     * @since 1.1.8
     */
    private String head;
}
