package cn.handyplus.menu.inventory;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.db.Page;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.service.MenuItemService;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 物品库
 *
 * @author handy
 * @since 1.7.7
 */
public class ItemGui {

    private ItemGui() {
    }

    private final static ItemGui INSTANCE = new ItemGui();

    public static ItemGui getInstance() {
        return INSTANCE;
    }

    /**
     * 创建gui
     *
     * @param player 玩家
     * @return gui
     */
    public Inventory createGui(Player player) {
        String title = ConfigUtil.ITEM_CONFIG.getString("title");
        title = PlaceholderApiUtil.set(player, title);
        int size = ConfigUtil.ITEM_CONFIG.getInt("size", BaseConstants.GUI_SIZE_54);
        String sound = ConfigUtil.ITEM_CONFIG.getString("sound");
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.ITEM.getType(), title, size, sound);
        handyInventory.setPlayer(player);
        this.setInventoryDate(handyInventory);
        return handyInventory.getInventory();
    }

    /**
     * 设置数据
     *
     * @param handyInventory gui
     */
    public void setInventoryDate(HandyInventory handyInventory) {
        // 基础设置
        handyInventory.setGuiType(GuiTypeEnum.ITEM.getType());
        // 1.刷新
        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());
        // 2.设置数据
        this.setData(handyInventory);
        // 3.设置功能性菜单
        this.setFunctionMenu(handyInventory);
    }

    /**
     * 设置数据
     *
     * @param handyInventory gui
     */
    private void setData(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();
        Map<Integer, Integer> map = handyInventory.getIntMap();
        String guiIndexStr = ConfigUtil.ITEM_CONFIG.getString("item.index");
        List<Integer> guiIndexList = StrUtil.strToIntList(guiIndexStr);
        handyInventory.setPageSize(guiIndexList.size());
        Page<MenuItem> page = MenuItemService.getInstance().page(handyInventory.getPageNum(), guiIndexList.size());
        handyInventory.setPageCount(page.getTotal());
        List<MenuItem> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        int i = 0;
        // 生成显示物品
        List<String> loreList = ConfigUtil.ITEM_CONFIG.getStringList("item.lore");
        for (MenuItem record : records) {
            Integer index = guiIndexList.get(i++);
            ItemStack itemStack = ItemStackUtil.itemStackDeserialize(record.getItemStack());
            ItemMeta itemMeta = ItemStackUtil.getItemMeta(itemStack);
            List<String> newLoreList = CollUtil.isNotEmpty(itemMeta.getLore()) ? itemMeta.getLore() : new ArrayList<>();
            newLoreList.addAll(loreList);
            newLoreList = ItemStackUtil.loreReplaceMap(newLoreList, this.replaceMap(record));
            itemMeta.setLore(BaseUtil.replaceChatColor(newLoreList));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(index, itemStack);
            map.put(index, record.getId());
        }
    }

    /**
     * 设置功能性菜单
     *
     * @param handyInventory GUI
     */
    private void setFunctionMenu(HandyInventory handyInventory) {
        // 设置翻页按钮
        Map<String, String> replacePageMap = HandyInventoryUtil.replacePageMap(handyInventory);
        HandyInventoryUtil.setButton(ConfigUtil.ITEM_CONFIG, handyInventory, "nextPage", replacePageMap);
        HandyInventoryUtil.setButton(ConfigUtil.ITEM_CONFIG, handyInventory, "previousPage", replacePageMap);
        // 自定义按钮
        HandyInventoryUtil.setCustomButton(ConfigUtil.ITEM_CONFIG, handyInventory, "custom");
    }

    /**
     * 变量map
     *
     * @param menuItem lore
     * @return map
     */
    private Map<String, String> replaceMap(MenuItem menuItem) {
        Map<String, String> map = MapUtil.newHashMapWithExpectedSize(4);
        map.put("id", menuItem.getId().toString());
        return map;
    }

}