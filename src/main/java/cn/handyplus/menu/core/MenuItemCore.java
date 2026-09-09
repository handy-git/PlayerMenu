package cn.handyplus.menu.core;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.item.ItemCompatUtil;
import cn.handyplus.lib.util.ItemMetaUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.param.MenuButtonParam;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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
     * @param menuItem        预查询的菜单物品
     * @return 菜单物品
     */
    public static ItemStack getMenuItem(@NotNull MenuButtonParam menuButtonParam, @Nullable MenuItem menuItem) {
        ItemStack itemStack;
        if (menuButtonParam.getId() > 0) {
            // ID 物品
            itemStack = getItemStackById(menuButtonParam, menuItem);
        } else {
            ItemStack compatItem = ItemCompatUtil.getItemStack(menuButtonParam.getMaterial());
            if (compatItem != null) {
                itemStack = compatItem;
                itemStack.setItemMeta(baseParam(menuButtonParam, ItemStackUtil.getItemMeta(itemStack)));
                return itemStack;
            }
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
     * @param menuItem        菜单物品
     */
    private static ItemStack getItemStackById(@NotNull MenuButtonParam menuButtonParam, @Nullable MenuItem menuItem) {
        if (menuItem == null) {
            return new ItemStack(Material.STONE);
        }
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

}
