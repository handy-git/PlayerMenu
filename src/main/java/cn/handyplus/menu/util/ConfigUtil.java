package cn.handyplus.menu.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 配置类
 *
 * @author handy
 */
public class ConfigUtil {
    public static Map<String, FileConfiguration> MENU_CONFIG_MAP;
    public static Map<String, String> COMMAND_MAP;
    public static Map<String, String> ITEM_MAP;
    public static Map<String, Boolean> PERMISSION_MAP;

    /**
     * 加载全部配置
     */
    public static void init() {
        // 加载config
        HandyConfigUtil.loadConfig();
        // 加载语言文件
        HandyConfigUtil.loadLangConfig(true);
        // 加载菜单文件
        MENU_CONFIG_MAP = new HashMap<>();
        COMMAND_MAP = new HashMap<>();
        ITEM_MAP = new HashMap<>();
        PERMISSION_MAP = new HashMap<>();
        // 读取目录下菜单文件
        if (!HandyConfigUtil.exists("menu/")) {
            HandyConfigUtil.load("zh_CN".equalsIgnoreCase(BaseConstants.CONFIG.getString("language")) ? "menu/menu.yml" : "menu/example.yml");
        }
        // 读取目录下菜单文件
        MENU_CONFIG_MAP = HandyConfigUtil.loadDirectory("menu/");
        for (String key : MENU_CONFIG_MAP.keySet()) {
            FileConfiguration fileConfiguration = MENU_CONFIG_MAP.get(key);
            // 命令
            String openCommand = fileConfiguration.getString("openCommand");
            if (StrUtil.isNotEmpty(openCommand)) {
                COMMAND_MAP.put(openCommand, key);
            }
            // 物品
            String openItem = fileConfiguration.getString("openItem");
            if (StrUtil.isNotEmpty(openItem)) {
                ITEM_MAP.put(openItem.toLowerCase(Locale.ROOT), key);
            }
            // 权限
            PERMISSION_MAP.put(key, fileConfiguration.getBoolean("permission"));
        }

        // 升级节点处理
        upConfig();
    }

    /**
     * 升级节点处理
     *
     * @since 1.1.3
     */
    private static void upConfig() {
        // 1.1.3 添加快捷键F操作
        HandyConfigUtil.setPathIsNotContains(BaseConstants.CONFIG, "shift.F.enable", false, Collections.singletonList("是否开启"), "config.yml");
        HandyConfigUtil.setPathIsNotContains(BaseConstants.CONFIG, "shift.F.menu", "menu.yml", Collections.singletonList("使用Shift+F打开的菜单"), "config.yml");
        // 1.6.2
        HandyConfigUtil.setPathIsNotContains(BaseConstants.CONFIG, "autoCreateId", true, Collections.singletonList("使用指令/plm create创建菜单是否自动生成ID"), "config.yml");
        HandyConfigUtil.loadConfig();
        // 1.1.7
        String language = "languages/" + BaseConstants.CONFIG.getString("language") + ".yml";
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "noTimeLimit", "&7需等待&a${time}&7秒,才可使用", null, language);
        // 1.2.0
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "noItem", "&8[&c✘&8] &7物品不足,出售失败", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "sellMsg", "&8[&a✔&8] &7出售成功", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "buyMsg", "&8[&a✔&8] &7购买成功", null, language);
        // 1.2.8
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "addItemMsg", "&8[&a!&8] &a背包不足，多余物品已掉落在地上请尽快捡起", null, language);
        // 1.3.1
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "playerNotOnline", "&8[&c✘&8] &7玩家 &a${player} &7不在线", null, language);
        // 1.3.7
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "noNumber", "&8[&c✘&8] &7请输入数字", null, language);
        // 1.4.2
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "playerCurrencySucceedMsg", "&a已成功加载PlayerCurrency 启用多货币经济兼容", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "playerCurrencyFailureMsg", "&7你的服务端没有安装PlayerCurrency 未启用多货币经济兼容", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "noBalance", "&8[&c✘&8] &7${type}&7不足,无法使用", null, language);
        // 1.4.3
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "sellOperatorReason", "&7玩家出售 &a${number} &7个物品 &a${name}", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "buyOperatorReason", "&7玩家购买 &a${number} &7个物品 &a${name}", null, language);
        // 1.4.5
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "money", "&7金币", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "point", "&7点券", null, language);
        // 1.5.2
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "clockFailureMsg", "&8[&c✘&8] &7你已经拥有菜单了", null, language);
        // 1.5.6
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "clickOperatorReason", "&8[&a✔&8] &7玩家点击菜单按钮 &a${name}", null, language);
        // 1.5.8
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "clearSucceedMsg", "&8[&a✔&8] &7清理成功,本次清理ID: ${id}", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "noInput", "&8[&c✘&8] &7禁止输入该内容 %", null, language);
        // 1.6.6
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "addMenuItemMsg", "&8[&a✔&8] &7新增成功,ID: ${id}", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "getMenuItemMsg", "&8[&c✘&8] &7不存在ID: ${id}", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "tabHelp.id", "请输入ID", null, language);
        HandyConfigUtil.setPathIsNotContains(BaseConstants.LANG_CONFIG, "tabHelp.number", "请输入数量", null, language);
        HandyConfigUtil.loadLangConfig(true);
    }

}