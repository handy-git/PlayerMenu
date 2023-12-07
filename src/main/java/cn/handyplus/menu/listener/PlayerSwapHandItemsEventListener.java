package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * 玩家用快捷键互换主手和副手的物品时触发本事件.
 *
 * @author handy
 * @since 1.1.3
 */
@HandyListener
public class PlayerSwapHandItemsEventListener implements Listener {

    /**
     * 处理快捷打开菜单.
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        boolean shiftF = ConfigUtil.CONFIG.getBoolean("shift.F.enable");
        if (!shiftF) {
            return;
        }
        event.setCancelled(true);
        String clockMenu = ConfigUtil.CONFIG.getString("shift.F.menu", "");
        MenuUtil.openGui(player, clockMenu);
    }

}
