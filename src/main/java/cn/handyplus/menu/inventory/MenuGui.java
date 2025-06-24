package cn.handyplus.menu.inventory;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemMetaUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.service.MenuItemService;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * @param player   玩家
     * @param menu     菜单
     * @param papiName 变量玩家
     * @return gui
     */
    public Inventory createGui(Player player, String menu, String papiName) {
        FileConfiguration fileConfiguration = ConfigUtil.MENU_CONFIG_MAP.get(menu);
        if (fileConfiguration == null) {
            return null;
        }
        String title = fileConfiguration.getString("title", menu);
        title = PlaceholderApiUtil.set(player, title);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.MENU.getType(), BaseUtil.replaceChatColor(title), fileConfiguration.getInt("size", BaseConstants.GUI_SIZE_54));
        handyInventory.setPlayer(player);
        handyInventory.setObj(fileConfiguration);
        handyInventory.setSearchType(papiName);
        this.setInventoryDate(handyInventory);
        // 播放打开菜单音效
        MenuUtil.playSound(player, fileConfiguration.getString("sound"));
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
        handyInventory.getObjMap().clear();
        Map<Integer, Object> objMap = handyInventory.getObjMap();
        Player player = handyInventory.getPlayer();
        String papiName = handyInventory.getSearchType();
        OfflinePlayer papiPlayer = StrUtil.isNotEmpty(papiName) ? BaseUtil.getOfflinePlayer(papiName) : null;
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
            MenuButtonParam menuButtonParam = getMenuButtonParam(memorySection, papiPlayer != null ? papiPlayer : player);
            // 判断是否有按钮权限
            if (StrUtil.isNotEmpty(menuButtonParam.getPermission()) && !player.hasPermission(menuButtonParam.getPermission())) {
                continue;
            }
            // 没有对应权限才能显示按钮
            if (StrUtil.isNotEmpty(menuButtonParam.getNotPermission()) && player.hasPermission(menuButtonParam.getNotPermission())) {
                continue;
            }
            // 判断是没次数隐藏
            if (MenuUtil.clickLimit(player, menuButtonParam.getId(), menuButtonParam.getLimitHide(), false)) {
                continue;
            }
            // 判断是CD中隐藏
            if (MenuUtil.clickCd(player, menuButtonParam.getId(), menuButtonParam.getCdHide(), false)) {
                continue;
            }
            // 物品显示数量
            int amount = menuButtonParam.getAmount() > 0 ? menuButtonParam.getAmount() : 1;
            if (StrUtil.isNotEmpty(menuButtonParam.getDynamicAmount())) {
                String dynamicAmount = PlaceholderApiUtil.set(player, menuButtonParam.getDynamicAmount());
                amount = NumberUtil.isNumericToInt(dynamicAmount, amount);
                amount = amount != 0 ? amount : 1;
            }
            for (Integer index : menuButtonParam.getIndexList()) {
                // 判断优先级
                if (objMap.containsKey(index)) {
                    MenuButtonParam menuParam = (MenuButtonParam) objMap.get(index);
                    if (menuParam.getPriority() != null && menuButtonParam.getPriority() != null &&
                            menuParam.getPriority() > menuButtonParam.getPriority()) {
                        continue;
                    }
                }
                ItemStack itemStack = ItemStackUtil.getItemStack(
                        menuButtonParam.getMaterial(), menuButtonParam.getName(),
                        menuButtonParam.getLoreList(), menuButtonParam.getIsEnchant(),
                        menuButtonParam.getCustomModelDataId(), menuButtonParam.getHideFlag(),
                        null, menuButtonParam.getHideEnchant());
                // 根据id进行特殊处理
                itemStack = getItemStackById(menuButtonParam, itemStack);
                itemStack.setAmount(amount);
                // 处理头颅物品
                setHead(menuButtonParam, itemStack);
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
    public static MenuButtonParam getMenuButtonParam(MemorySection memorySection, OfflinePlayer player) {
        String name = memorySection.getString("name");
        List<String> loreList = memorySection.getStringList("lore");
        String head = memorySection.getString("head");
        String headBase = memorySection.getString("headBase");
        // 变量处理
        if (player != null) {
            name = PlaceholderApiUtil.set(player, name);
            loreList = PlaceholderApiUtil.set(player, loreList);
            head = PlaceholderApiUtil.set(player, head);
        }
        String indexStrList = memorySection.getString("index");
        List<Integer> indexList = StrUtil.strToIntList(indexStrList);
        String material = memorySection.getString("material");
        int customModelDataId = memorySection.getInt("custom-model-data");
        int priority = memorySection.getInt("priority");
        List<String> commands = memorySection.getStringList("commands");
        List<String> leftActions = memorySection.getStringList("actions.left");
        List<String> rightActions = memorySection.getStringList("actions.right");
        List<String> conditions = memorySection.getStringList("conditions");
        String sound = memorySection.getString("sound");
        String clickType = memorySection.getString("clickType");
        String failSound = memorySection.getString("failSound");
        boolean isEnchant = memorySection.getBoolean("isEnchant", false);
        boolean hideFlag = memorySection.getBoolean("hideFlag", true);
        boolean hideEnchant = memorySection.getBoolean("hideEnchant", true);
        int point = memorySection.getInt("point");
        String ply = memorySection.getString("ply");
        int money = memorySection.getInt("money");
        int limit = memorySection.getInt("limit");
        int limitHide = memorySection.getInt("limitHide");
        int cd = memorySection.getInt("cd");
        int cdHide = memorySection.getInt("cdHide");
        int id = memorySection.getInt("id", 0);
        String permission = memorySection.getString("permission");
        String notPermission = memorySection.getString("notPermission");
        // 商店配置
        String shopType = memorySection.getString("shopType");
        String shopMaterial = memorySection.getString("shopMaterial");
        String shopMoney = memorySection.getString("shopMoney");
        String shopPoint = memorySection.getString("shopPoint");
        String shopCurrency = memorySection.getString("shopCurrency");
        int amount = memorySection.getInt("amount");
        String dynamicAmount = memorySection.getString("dynamicAmount");
        String input = memorySection.getString("input");
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
        menuButtonParam.setPly(ply);
        menuButtonParam.setLimit(limit);
        menuButtonParam.setLimitHide(limitHide);
        menuButtonParam.setCd(cd);
        menuButtonParam.setCdHide(cdHide);
        menuButtonParam.setId(id != 0 ? id : null);
        menuButtonParam.setHead(head);
        menuButtonParam.setHeadBase(headBase);
        menuButtonParam.setPermission(permission);
        menuButtonParam.setAmount(amount);
        menuButtonParam.setDynamicAmount(dynamicAmount);
        menuButtonParam.setNotPermission(notPermission);
        menuButtonParam.setPriority(priority);
        Map<String, List<String>> actions = new HashMap<>();
        if (CollUtil.isNotEmpty(leftActions)) {
            actions.put("left", leftActions);
        }
        if (CollUtil.isNotEmpty(rightActions)) {
            actions.put("right", rightActions);
        }
        menuButtonParam.setActions(actions);
        menuButtonParam.setClickType(clickType);
        // 扩展商店属性
        menuButtonParam.setShopType(shopType);
        menuButtonParam.setShopMaterial(shopMaterial);
        menuButtonParam.setShopMoney(shopMoney);
        menuButtonParam.setShopPoint(shopPoint);
        menuButtonParam.setShopCurrency(shopCurrency);
        menuButtonParam.setInput(input);
        return menuButtonParam;
    }

    /**
     * 根据id进行替换
     *
     * @param menuButtonParam 菜单参数
     * @param itemStack       菜单
     */
    public static ItemStack getItemStackById(MenuButtonParam menuButtonParam, ItemStack itemStack) {
        if (menuButtonParam.getId() == null || menuButtonParam.getId() < 1) {
            return itemStack;
        }
        Optional<MenuItem> menuItemOptional = MenuItemService.getInstance().findById(menuButtonParam.getId());
        if (!menuItemOptional.isPresent()) {
            return itemStack;
        }
        MenuItem menuItem = menuItemOptional.get();
        ItemStack item = ItemStackUtil.itemStackDeserialize(menuItem.getItemStack());
        ItemMeta newItemMeta = ItemStackUtil.getItemMeta(item);
        ItemMeta oldItemMeta = ItemStackUtil.getItemMeta(itemStack);
        // 基础属性
        newItemMeta.setDisplayName(oldItemMeta.getDisplayName());
        newItemMeta.setLore(oldItemMeta.getLore());
        ItemMetaUtil.setCustomModelData(newItemMeta, menuButtonParam.getCustomModelDataId());
        // 扩展属性
        if (menuButtonParam.getIsEnchant()) {
            ItemMetaUtil.setEnchant(newItemMeta);
        }
        if (menuButtonParam.getHideFlag()) {
            ItemMetaUtil.hideAttributes(newItemMeta);
        }
        if (menuButtonParam.getHideEnchant()) {
            ItemMetaUtil.hideEnchant(newItemMeta);
        }
        item.setItemMeta(newItemMeta);
        return item;
    }

    /**
     * 设置头颅
     *
     * @param menuButtonParam 菜单参数
     * @param itemStack       菜单
     */
    public static void setHead(MenuButtonParam menuButtonParam, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof SkullMeta)) {
            return;
        }
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        if (StrUtil.isNotEmpty(menuButtonParam.getHead())) {
            ItemMetaUtil.setOwner(skullMeta, menuButtonParam.getHead());
            itemStack.setItemMeta(skullMeta);
            return;
        }
        if (StrUtil.isNotEmpty(menuButtonParam.getHeadBase())) {
            ItemMetaUtil.setSkull(skullMeta, menuButtonParam.getHeadBase());
            itemStack.setItemMeta(skullMeta);
        }
    }

}