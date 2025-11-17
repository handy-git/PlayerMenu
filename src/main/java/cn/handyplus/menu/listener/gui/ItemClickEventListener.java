package cn.handyplus.menu.listener.gui;

import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.inventory.ItemGui;
import cn.handyplus.menu.service.MenuItemService;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

/**
 * 物品库
 *
 * @author handy
 * @since 1.7.7
 */
public class ItemClickEventListener implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.ITEM.getType();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        Integer pageNum = handyInventory.getPageNum();
        Integer pageCount = handyInventory.getPageCount();
        Map<Integer, Integer> map = handyInventory.getIntMap();

        // 上一页
        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.ITEM_CONFIG, "previousPage")) {
            if (pageNum > 1) {
                handyInventory.setPageNum(handyInventory.getPageNum() - 1);
                ItemGui.getInstance().setInventoryDate(handyInventory);
            }
            return;
        }
        // 下一页
        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.ITEM_CONFIG, "nextPage")) {
            if (pageNum + 1 <= pageCount) {
                handyInventory.setPageNum(handyInventory.getPageNum() + 1);
                ItemGui.getInstance().setInventoryDate(handyInventory);
            }
            return;
        }

        //  点击删除
        Integer id = map.get(rawSlot);
        if (id != null) {
            MenuItemService.getInstance().delById(id);
            ItemGui.getInstance().setInventoryDate(handyInventory);
        }
    }

}