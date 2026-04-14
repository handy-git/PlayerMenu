package cn.handyplus.menu.core;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.ItemMetaUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.service.MenuItemService;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * @author handy
 */
public final class MenuItemCore {

    private MenuItemCore() {
    }

    /**
     * 获取菜单物品
     *
     * @param menuButtonParam 菜单参数
     * @return 菜单物品
     */
    public static ItemStack getMenuItem(@NotNull MenuButtonParam menuButtonParam) {
        ItemStack itemStack;
        if (menuButtonParam.getId() > 0) {
            // ID 物品
            itemStack = getItemStackById(menuButtonParam);
        } else if (menuButtonParam.getMaterial().contains(MenuConstants.CE)) {
            // CE 物品
            itemStack = getCraftEngine(menuButtonParam);
        } else {
            // 普通物品
            itemStack = ItemStackUtil.getItemStack(
                    menuButtonParam.getMaterial(), menuButtonParam.getName(),
                    menuButtonParam.getLoreList(), menuButtonParam.getIsEnchant(),
                    menuButtonParam.getCustomModelDataId(), menuButtonParam.getHideFlag(),
                    null, menuButtonParam.getHideEnchant(), null,
                    menuButtonParam.getTooltipStyle(), menuButtonParam.getItemModel());
            // 处理头颅物品
            setHead(menuButtonParam, itemStack);
        }
        return itemStack;
    }

    /**
     * 根据id进行替换
     *
     * @param menuButtonParam 菜单参数
     */
    private static ItemStack getItemStackById(@NotNull MenuButtonParam menuButtonParam) {
        Optional<MenuItem> menuItemOptional = MenuItemService.getInstance().findById(menuButtonParam.getId());
        if (!menuItemOptional.isPresent()) {
            return new ItemStack(Material.STONE);
        }
        MenuItem menuItem = menuItemOptional.get();
        ItemStack itemStack = ItemStackUtil.itemStackDeserialize(menuItem.getItemStack());
        ItemMeta newItemMeta = ItemStackUtil.getItemMeta(itemStack);
        // 设置基础属性
        itemStack.setItemMeta(baseParam(menuButtonParam, newItemMeta));
        return itemStack;
    }

    /**
     * 设置基础属性
     *
     * @param menuButtonParam 菜单参数
     * @param itemMeta        物品属性
     * @return 物品属性
     */
    private static ItemMeta baseParam(@NonNull MenuButtonParam menuButtonParam, @NotNull ItemMeta itemMeta) {
        // 基础属性
        ItemMetaUtil.setDisplayName(itemMeta, menuButtonParam.getName());
        ItemMetaUtil.setLore(itemMeta, menuButtonParam.getLoreList());
        // 材质包属性
        ItemMetaUtil.setCustomModelData(itemMeta, menuButtonParam.getCustomModelDataId());
        ItemMetaUtil.setTooltipStyle(itemMeta, menuButtonParam.getTooltipStyle());
        ItemMetaUtil.setItemModel(itemMeta, menuButtonParam.getItemModel());
        // 扩展属性
        if (menuButtonParam.getIsEnchant()) {
            ItemMetaUtil.setEnchant(itemMeta);
        }
        if (menuButtonParam.getHideFlag()) {
            ItemMetaUtil.hideAttributes(itemMeta);
        }
        if (menuButtonParam.getHideEnchant()) {
            ItemMetaUtil.hideEnchant(itemMeta);
        }
        return itemMeta;
    }

    /**
     * 设置头颅
     *
     * @param menuButtonParam 菜单参数
     * @param itemStack       菜单
     */
    private static void setHead(@NotNull MenuButtonParam menuButtonParam, @NotNull ItemStack itemStack) {
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

    /**
     * CE插件物品
     *
     * @param menuButtonParam 物品配置
     * @return CE插件物品
     */
    private static @NotNull ItemStack getCraftEngine(@NotNull MenuButtonParam menuButtonParam) {
        if (!PlayerMenu.USE_CE) {
            throw new RuntimeException("not fount CE");
        }
        String material = menuButtonParam.getMaterial();
        material = material.replace(MenuConstants.CE, "");
        String[] itemStr = material.split(":");
        String namespace = itemStr[0].trim();
        String value = itemStr[1].trim();
        CustomItem<ItemStack> customItem = CraftEngineItems.byId(Key.of(namespace, value));
        if (customItem == null) {
            return new ItemStack(Material.STONE);
        }
        // 创建CE物品
        ItemStack itemStack = customItem.buildItemStack();
        ItemMeta newItemMeta = ItemStackUtil.getItemMeta(itemStack);
        // 设置基础属性
        itemStack.setItemMeta(baseParam(menuButtonParam, newItemMeta));
        return itemStack;
    }

}
