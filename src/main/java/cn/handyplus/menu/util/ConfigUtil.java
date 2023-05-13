package cn.handyplus.menu.util;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;

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

        // 加载示例
        HandyConfigUtil.load("menu/menu.yml");

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
        }
    }

}