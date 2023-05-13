package cn.handyplus.menu.inventory;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.CREATE.getType(), BaseUtil.replaceChatColor(title), size);
        // 设置数据
        handyInventory.setPageNum(1);
        handyInventory.setPlayer(player);
        handyInventory.setSearchType(PlayerMenu.getInstance().getDataFolder() + "/menu/" + menu);
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
        handyInventory.setGuiType(GuiTypeEnum.CREATE.getType());
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
        for (String key : values.keySet()) {
            // 二级目录
            MemorySection memorySection = (MemorySection) values.get(key);
            if (memorySection == null) {
                continue;
            }
            MenuButtonParam menuButtonParam = MenuGui.getMenuButtonParam(memorySection, null);
            for (Integer index : menuButtonParam.getIndexList()) {
                ItemStack itemStack = ItemStackUtil.getItemStack(
                        menuButtonParam.getMaterial(), menuButtonParam.getName(),
                        menuButtonParam.getLoreList(), menuButtonParam.getIsEnchant(),
                        menuButtonParam.getCustomModelDataId(), menuButtonParam.getHideFlag(),
                        null, menuButtonParam.getHideEnchant());
                // 根据id进行特殊处理
                itemStack = MenuGui.getItemStackById(menuButtonParam, itemStack);
                ItemStackUtil.setPersistentData(itemStack, JsonUtil.toJson(menuButtonParam), MenuConstants.PREFIX);
                inventory.setItem(index, itemStack);
            }
        }
    }

}