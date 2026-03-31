package cn.handyplus.menu.listener.gui;

import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.core.MenuCore;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * 二次确定
 *
 * @author handy
 */
public class ConfirmClickEvent implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.CONFIRM.getType();
    }

    @Override
    public void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        Player player = handyInventory.getPlayer();
        if (player == null) {
            return;
        }
        // 确定
        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.CONFIRM_CONFIG, "confirm")) {
            this.confirm(player, handyInventory);
            return;
        }
        // 取消或返回
        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.CONFIRM_CONFIG, "cancel")
                || HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.CONFIRM_CONFIG, "back")) {
            this.back(player, handyInventory);
        }
    }

    /**
     * 确认执行
     *
     * @param player         玩家
     * @param handyInventory 当前gui
     */
    private void confirm(Player player, HandyInventory handyInventory) {
        if (!(handyInventory.getObj() instanceof MenuButtonParam)) {
            handyInventory.syncClose();
            return;
        }
        MenuButtonParam menuButtonParam = (MenuButtonParam) handyInventory.getObj();
        // 执行菜单逻辑
        MenuCore.executeMenu(player, menuButtonParam);
        // 返回菜单
        back(player, handyInventory);
    }

    /**
     * 返回菜单
     *
     * @param player         玩家
     * @param handyInventory 当前gui
     */
    private void back(Player player, HandyInventory handyInventory) {
        String menu = handyInventory.getStrMap().get(0);
        String papiName = handyInventory.getSearchType();
        MenuUtil.asyncOpenGui(player, menu, papiName);
    }

}