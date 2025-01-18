package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.HandyHttpUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * 登录事件
 *
 * @author handy
 */
@HandyListener
public class PlayerJoinEventListener implements Listener {

    /**
     * 玩家进入发送菜单
     *
     * @param event 事件
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            if (!BaseConstants.CONFIG.getBoolean("clock.enable")) {
                return;
            }
            Player player = event.getPlayer();
            PlayerInventory inventory = player.getInventory();
            ItemStack itemStack = MenuUtil.getClock();
            for (ItemStack stack : inventory) {
                if (ItemStackUtil.isSimilar(itemStack, stack)) {
                    return;
                }
            }
            inventory.addItem(itemStack);
        });
    }

    /**
     * op进入服务器发送更新提醒
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onOpPlayerJoin(PlayerJoinEvent event) {
        HandyHttpUtil.checkVersion(event.getPlayer());
    }

}
