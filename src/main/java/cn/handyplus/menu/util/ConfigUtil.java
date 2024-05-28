package cn.handyplus.menu.util;

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
    public static FileConfiguration CONFIG, LANG_CONFIG;
    public static Map<String, FileConfiguration> MENU_CONFIG_MAP;
    public static Map<String, String> COMMAND_MAP;
    public static Map<String, String> ITEM_MAP;
    public static Map<String, Boolean> PERMISSION_MAP;

    /**
     * 加载全部配置
     */
    public static void init() {
        // 加载config
        CONFIG = HandyConfigUtil.loadConfig();
        // 加载语言文件
        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language"));
        // 加载菜单文件
        MENU_CONFIG_MAP = new HashMap<>();
        COMMAND_MAP = new HashMap<>();
        ITEM_MAP = new HashMap<>();
        PERMISSION_MAP = new HashMap<>();
        // 读取目录下菜单文件
        if (!HandyConfigUtil.exists("menu/")) {
            HandyConfigUtil.load("menu/menu.yml");
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
        HandyConfigUtil.setPathIsNotContains(CONFIG, "shift.F.enable", false, Collections.singletonList("是否开启"), "config.yml");
        HandyConfigUtil.setPathIsNotContains(CONFIG, "shift.F.menu", "menu.yml", Collections.singletonList("使用Shift+F打开的菜单"), "config.yml");
        CONFIG = HandyConfigUtil.load("config.yml");
        // 1.1.7
        String language = "languages/" + CONFIG.getString("language") + ".yml";
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "noTimeLimit", "&7需等待&a${time}&7秒,才可使用", null, language);
        // 1.2.0
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "noItem", "&8[&c✘&8] &7物品不足,出售失败", null, language);
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "sellMsg", "&8[&a✔&8] &7出售成功", null, language);
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "buyMsg", "&8[&a✔&8] &7购买成功", null, language);
        // 1.2.8
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "addItemMsg", "&8[&a!&8] &a背包不足，多余物品已掉落在地上请尽快捡起", null, language);
        // 1.3.1
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "playerNotOnline", "&8[&c✘&8] &7玩家 &a${player} &7不在线", null, language);
        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language"), true);
    }

}