package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.core.HideInventoryUtil;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * 玩家菜单隐藏背包监听.
 *
 * @author handy
 */
@HandyListener
public class MenuInventoryHideListener implements Listener {

    /**
     * 玩家菜单打开时记录隐藏状态.
     *
     * @param event 背包打开事件
     */
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player) || this.isPlayerMenu(event.getInventory().getHolder())) {
            return;
        }
        HandyInventory handyInventory = (HandyInventory) event.getInventory().getHolder();
        // 菜单配置优先于全局配置，未配置时继承全局值。
        if (this.isHideEnabled(handyInventory)) {
            HideInventoryUtil.hide((Player) event.getPlayer(), event.getInventory().getSize());
        }
    }

    /**
     * 玩家菜单关闭时恢复背包显示.
     *
     * @param event 背包关闭事件
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player) || this.isPlayerMenu(event.getInventory().getHolder())) {
            return;
        }
        HideInventoryUtil.show((Player) event.getPlayer());
    }

    /**
     * 判断当前背包是否应跳过隐藏处理.
     *
     * @param holder 背包持有者
     * @return true 表示不是需要处理的玩家菜单
     */
    private boolean isPlayerMenu(InventoryHolder holder) {
        if (!(holder instanceof HandyInventory)) {
            return true;
        }
        String guiType = ((HandyInventory) holder).getGuiType();
        return !GuiTypeEnum.MENU.getType().equals(guiType) && !GuiTypeEnum.CONFIRM.getType().equals(guiType);
    }

    /**
     * 获取当前菜单实际生效的隐藏配置.
     *
     * @param handyInventory 当前菜单
     * @return 是否隐藏背包物品
     */
    private boolean isHideEnabled(HandyInventory handyInventory) {
        boolean globalHide = BaseConstants.CONFIG.getBoolean("hideInventory", true);
        if (GuiTypeEnum.MENU.getType().equals(handyInventory.getGuiType())
                && handyInventory.getObj() instanceof FileConfiguration) {
            return ((FileConfiguration) handyInventory.getObj()).getBoolean("hideInventory", globalHide);
        }
        if (GuiTypeEnum.CONFIRM.getType().equals(handyInventory.getGuiType())) {
            String menu = handyInventory.getStrMap().get(0);
            FileConfiguration menuConfig = ConfigUtil.MENU_CONFIG_MAP.get(menu);
            if (menuConfig != null) {
                return menuConfig.getBoolean("hideInventory", globalHide);
            }
        }
        return globalHide;
    }
}
