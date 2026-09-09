package cn.handyplus.menu.inventory;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.ItemMetaUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.core.MenuItemCore;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.service.MenuItemService;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 编辑gui
 *
 * @author handy
 */
public class ViewGui {
    private ViewGui() {
    }

    private final static ViewGui INSTANCE = new ViewGui();

    public static ViewGui getInstance() {
        return INSTANCE;
    }

    /**
     * 创建gui
     *
     * @param player 玩家
     * @param menu   菜单
     * @return gui
     */
    public Inventory createGui(Player player, String menu) {
        FileConfiguration fileConfiguration = ConfigUtil.MENU_CONFIG_MAP.get(menu);
        if (fileConfiguration == null) {
            return null;
        }
        String title = fileConfiguration.getString("title", menu);
        title = PlaceholderApiUtil.set(player, title);
        int size = fileConfiguration.getInt("size", BaseConstants.GUI_SIZE_54);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.VIEW.getType(), title, size);
        handyInventory.setPlayer(player);
        handyInventory.setSearchType(PlayerMenu.INSTANCE.getDataFolder() + "/menu/" + menu);
        handyInventory.setObj(fileConfiguration);
        handyInventory.setToCancel(false);
        handyInventory.setId(size);
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
        handyInventory.setGuiType(GuiTypeEnum.VIEW.getType());
        // 1. 刷新
        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());
        // 2.设置功能性菜单
        this.setFunctionMenu(handyInventory);
    }

    /**
     * 设置功能性菜单
     *
     * @param handyInventory GUI
     */
    private void setFunctionMenu(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();
        // 获取菜单
        FileConfiguration fileConfiguration = (FileConfiguration) handyInventory.getObj();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("menu");
        if (configurationSection == null) {
            return;
        }
        // 一级目录
        Map<String, Object> values = configurationSection.getValues(false);
        Map<String, MenuButtonParam> menuButtonParamMap = new LinkedHashMap<>();
        Set<Integer> menuItemIdSet = new HashSet<>();
        for (String key : values.keySet()) {
            // 二级目录
            MemorySection memorySection = (MemorySection) values.get(key);
            if (memorySection == null) {
                continue;
            }
            MenuButtonParam menuButtonParam = MenuGui.getMenuButtonParam(memorySection, handyInventory.getPlayer());
            menuButtonParamMap.put(key, menuButtonParam);
            if (menuButtonParam.getId() > 0) {
                menuItemIdSet.add(menuButtonParam.getId());
            }
        }
        Map<Integer, MenuItem> menuItemMap = MenuItemService.getInstance().findMapByIds(new ArrayList<>(menuItemIdSet));
        for (Map.Entry<String, MenuButtonParam> entry : menuButtonParamMap.entrySet()) {
            String key = entry.getKey();
            MenuButtonParam menuButtonParam = entry.getValue();
            ItemStack itemStack = MenuItemCore.getMenuItem(menuButtonParam, menuItemMap.get(menuButtonParam.getId()));
            this.setMenuKey(itemStack, key);
            for (Integer index : menuButtonParam.getIndexList()) {
                inventory.setItem(index, itemStack.clone());
            }
        }
    }

    /**
     * 设置编辑菜单key.
     *
     * @param itemStack 物品
     * @param key       菜单key
     */
    @SuppressWarnings("deprecation")
    private void setMenuKey(ItemStack itemStack, String key) {
        if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_14.getVersionId()) {
            ItemStackUtil.setPersistentData(itemStack, key, MenuConstants.PREFIX);
            return;
        }
        ItemMeta itemMeta = ItemStackUtil.getItemMeta(itemStack);
        List<String> loreList = itemMeta.getLore() == null ? new ArrayList<>() : new ArrayList<>(itemMeta.getLore());
        loreList.add(MenuConstants.VIEW_KEY_PREFIX + key);
        ItemMetaUtil.setLore(itemMeta, loreList);
        itemStack.setItemMeta(itemMeta);
    }

}
