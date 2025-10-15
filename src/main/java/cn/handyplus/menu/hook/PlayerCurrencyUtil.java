package cn.handyplus.menu.hook;

import cn.handyplus.currency.api.PlayerCurrencyApi;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.PlayerMenu;
import org.bukkit.entity.Player;

/**
 * 玩家多货币插件 PlayerCurrency 兼容
 *
 * @author handy
 * @since 1.4.2
 */
public class PlayerCurrencyUtil {

    /**
     * 点击购买
     *
     * @param player 玩家
     * @param type   类型
     * @param price  价格
     */
    public static boolean buy(Player player, String type, long price, String operatorReason) {
        // 多经济是否加载
        if (!PlayerMenu.USE_PLY) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("playerCurrencyFailureMsg"));
            return false;
        }
        // 扣除余额
        return PlayerCurrencyApi.take(player.getUniqueId(), type, price, "PlayerMenu", operatorReason);
    }

    /**
     * 点击给予
     *
     * @param player 玩家
     * @param type   类型
     * @param price  价格
     */
    public static void give(Player player, String type, long price, String operatorReason) {
        if (price == 0) {
            return;
        }
        // 多经济是否加载
        if (!PlayerMenu.USE_PLY) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("playerCurrencyFailureMsg"));
            return;
        }
        // 给予余额
        PlayerCurrencyApi.give(player.getUniqueId(), type, price, "PlayerMenu", operatorReason);
    }

    /**
     * 查询玩家余额
     *
     * @param player 玩家
     * @return 玩家余额
     */
    public static long getPlayerBalance(Player player, String type) {
        if (!PlayerMenu.USE_PLY || player == null || StrUtil.isEmpty(type)) {
            return 0;
        }
        return PlayerCurrencyApi.look(player.getUniqueId(), type);
    }

    /**
     * 获取货币描述
     *
     * @param type 类型
     * @return 描述
     */
    public static String getDesc(String type) {
        if (!PlayerMenu.USE_PLY || StrUtil.isEmpty(type)) {
            return type;
        }
        return PlayerCurrencyApi.getDesc(type);
    }

}
