package cn.handyplus.menu.inventory;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
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

import java.util.List;
import java.util.Map;

/**
 * 生成gui
 *
 * @author handy
 */
public class MenuGui {
    private MenuGui() {
    }

    private final static MenuGui INSTANCE = new MenuGui();

    public static MenuGui getInstance() {
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
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.MENU.getType(), BaseUtil.replaceChatColor(title), fileConfiguration.getInt("size", BaseConstants.GUI_SIZE_54));
        // 设置数据
        handyInventory.setPageNum(1);
        handyInventory.setPlayer(player);
        handyInventory.setObj(fileConfiguration);
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
        handyInventory.setGuiType(GuiTypeEnum.MENU.getType());
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
        Map<Integer, Object> objMap = handyInventory.getObjMap();
        Player player = handyInventory.getPlayer();
        FileConfiguration fileConfiguration = (FileConfiguration) handyInventory.getObj();
        // 获取菜单
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
            MenuButtonParam menuButtonParam = getMenuButtonParam(memorySection, player);
            for (Integer index : menuButtonParam.getIndexList()) {
                ItemStack itemStack = ItemStackUtil.getItemStack(
                        menuButtonParam.getMaterial(), menuButtonParam.getName(),
                        menuButtonParam.getLoreList(), menuButtonParam.getIsEnchant(),
                        menuButtonParam.getCustomModelDataId(), menuButtonParam.getHideFlag(),
                        null, menuButtonParam.getHideEnchant());
                // 根据id进行特殊处理
                itemStack = getItemStackById(menuButtonParam, itemStack);
                inventory.setItem(index, itemStack);
                objMap.put(index, menuButtonParam);
            }
        }
    }

    /**
     * 获取 按钮参数
     *
     * @param memorySection 节点
     * @param player        玩家
     * @return 按钮参数
     */
    public static MenuButtonParam getMenuButtonParam(MemorySection memorySection, Player player) {
        String name = memorySection.getString("name");
        List<String> loreList = memorySection.getStringList("lore");
        // 变量处理
        if (player != null) {
            name = PlaceholderApiUtil.set(player, name);
            loreList = PlaceholderApiUtil.set(player, loreList);
        }
        String indexStrList = memorySection.getString("index");
        List<Integer> indexList = StrUtil.strToIntList(indexStrList);
        String material = memorySection.getString("material");
        int customModelDataId = memorySection.getInt("custom-model-data");
        List<String> commands = memorySection.getStringList("commands");
        List<String> conditions = memorySection.getStringList("conditions");
        String sound = memorySection.getString("sound");
        String failSound = memorySection.getString("failSound");
        boolean isEnchant = memorySection.getBoolean("isEnchant", false);
        boolean hideFlag = memorySection.getBoolean("hideFlag", true);
        boolean hideEnchant = memorySection.getBoolean("hideEnchant", true);
        int point = memorySection.getInt("point");
        int money = memorySection.getInt("money");
        int limit = memorySection.getInt("limit");
        int id = memorySection.getInt("id");
        // 构建类型
        MenuButtonParam menuButtonParam = new MenuButtonParam();
        // 基础属性
        menuButtonParam.setName(name);
        menuButtonParam.setLoreList(loreList);
        menuButtonParam.setIndexList(indexList);
        menuButtonParam.setMaterial(material);
        menuButtonParam.setCustomModelDataId(customModelDataId);
        menuButtonParam.setSound(sound);
        menuButtonParam.setFailSound(failSound);
        // 扩展属性
        menuButtonParam.setCommands(commands);
        menuButtonParam.setConditions(conditions);
        menuButtonParam.setIsEnchant(isEnchant);
        menuButtonParam.setHideEnchant(hideEnchant);
        menuButtonParam.setHideFlag(hideFlag);
        menuButtonParam.setPoint(point);
        menuButtonParam.setMoney(money);
        menuButtonParam.setLimit(limit);
        menuButtonParam.setId(id);
        return menuButtonParam;
    }

    /**
     * 根据id进行替换
     *
     * @param menuButtonParam 菜单参数
     * @param itemStack       菜单
     */
    public static ItemStack getItemStackById(MenuButtonParam menuButtonParam, ItemStack itemStack) {
        if (menuButtonParam.getId() < 1) {
            return itemStack;
        }
        MenuItem menuItem = MenuItemService.getInstance().findById(menuButtonParam.getId());
        if (menuItem == null) {
            return itemStack;
        }
        ItemStack item = ItemStackUtil.itemStackDeserialize(menuItem.getItemStack());
        ItemMeta newItemMeta = ItemStackUtil.getItemMeta(item);
        ItemMeta oldItemMeta = ItemStackUtil.getItemMeta(itemStack);
        // 基础属性
        newItemMeta.setDisplayName(oldItemMeta.getDisplayName());
        newItemMeta.setLore(oldItemMeta.getLore());
        ItemStackUtil.setCustomModelData(newItemMeta, menuButtonParam.getCustomModelDataId());
        // 扩展属性
        if (menuButtonParam.getIsEnchant()) {
            ItemStackUtil.setEnchant(newItemMeta);
        }
        if (menuButtonParam.getHideFlag()) {
            ItemStackUtil.hideAttributes(newItemMeta);
        }
        if (menuButtonParam.getHideEnchant()) {
            ItemStackUtil.hideEnchant(newItemMeta);
        }
        item.setItemMeta(newItemMeta);
        return item;
    }

}