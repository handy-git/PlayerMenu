package cn.handyplus.menu.hook;

import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.PlayerMenu;
import org.bukkit.entity.Player;

/**
 * 金币util
 *
 * @author handy
 */
public class VaultUtil {

    /**
     * 点击购买
     *
     * @param player 玩家
     * @param price  价格
     */
    public static boolean buy(Player player, int price) {
        // 查询是否开启经济系统
        if (PlayerMenu.getEconomy() == null) {
            player.sendMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return false;
        }
        // 查询玩家余额是否够
        if (!PlayerMenu.getEconomy().has(player, price)) {
            return false;
        }
        PlayerMenu.getEconomy().withdrawPlayer(player, price);
        return true;
    }

    /**
     * 点击给予
     *
     * @param player 玩家
     * @param price  价格
     */
    public static void give(Player player, int price) {
        if (price == 0) {
            return;
        }
        // 查询是否开启经济系统
        if (PlayerMenu.getEconomy() == null) {
            player.sendMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        PlayerMenu.getEconomy().depositPlayer(player, price);
    }

    /**
     * 查询玩家金币
     *
     * @param player 玩家
     * @return 玩家金币
     */
    public static double getPlayerVault(Player player) {
        if (PlayerMenu.getEconomy() == null || player == null) {
            return 0.0;
        }
        return PlayerMenu.getEconomy().getBalance(player);
    }

}
