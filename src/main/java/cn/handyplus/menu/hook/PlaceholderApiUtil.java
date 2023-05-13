package cn.handyplus.menu.hook;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.menu.PlayerMenu;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 变量工具类
 *
 * @author handy
 */
public class PlaceholderApiUtil {

    /**
     * 替换变量
     *
     * @param player 玩家
     * @param str    字符串
     * @return 新字符串
     */
    public static String set(Player player, String str) {
        if (PlayerMenu.USE_PAPI && player != null && StrUtil.isNotEmpty(str)) {
            return PlaceholderAPI.setPlaceholders(player, str);
        }
        return str;
    }

    /**
     * 替换变量
     *
     * @param player  玩家
     * @param strList 字符串集合
     * @return 新字符串集合
     */
    public static List<String> set(Player player, List<String> strList) {
        if (PlayerMenu.USE_PAPI && player != null && CollUtil.isNotEmpty(strList)) {
            return PlaceholderAPI.setPlaceholders(player, strList);
        }
        return strList;
    }

}