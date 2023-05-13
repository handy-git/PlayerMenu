package cn.handyplus.menu.hook;

import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.PlayerMenu;
import org.bukkit.entity.Player;

/**
 * 点券util
 *
 * @author handy
 */
public class PlayerPointsUtil {

    /**
     * 点击购买
     *
     * @param player 玩家
     * @param price  价格
     */
    public static boolean buy(Player player, int price) {
        // 点券是否加载
        if (PlayerMenu.getPlayerPoints() == null) {
            player.sendMessage(BaseUtil.getLangMsg("playerPointsFailureMsg"));
            return false;
        }
        // 扣除点券
        return PlayerMenu.getPlayerPoints().getAPI().take(player.getUniqueId(), price);
    }

    /**
     * 查询玩家点券
     *
     * @param player 玩家
     * @return 玩家点券
     */
    public static int getPlayerPoints(Player player) {
        if (PlayerMenu.getPlayerPoints() == null || player == null) {
            return 0;
        }
        return PlayerMenu.getPlayerPoints().getAPI().look(player.getUniqueId());
    }

}
