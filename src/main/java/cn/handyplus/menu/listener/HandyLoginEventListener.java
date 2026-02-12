package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.internal.HandyLoginEvent;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import cn.handyplus.lib.util.HandyHttpUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.constants.MenuConstants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * 登录事件
 *
 * @author handy
 */
@HandyListener
public class HandyLoginEventListener implements Listener {

    /**
     * 玩家进入发送菜单
     *
     * @param event 事件
     */
    @EventHandler
    public void onJoin(HandyLoginEvent event) {
        Player player = event.getPlayer();
        // OP 进入服务器发送更新提醒
        HandyHttpUtil.checkVersion(player);
        // 玩家进入发送菜单
        boolean clockEnable = BaseConstants.CONFIG.getBoolean("clock.enable");
        if (!clockEnable) {
            return;
        }
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            PlayerInventory inventory = player.getInventory();
            ItemStack itemStack = MenuConstants.CLOCK;
            for (ItemStack stack : inventory) {
                if (ItemStackUtil.isSimilar(itemStack, stack)) {
                    return;
                }
            }
            inventory.addItem(itemStack);
        });
    }

}
