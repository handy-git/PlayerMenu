package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 关闭view编辑菜单保存index.
 *
 * @author handy
 */
@HandyListener
public class MenuEditEventListener implements Listener {

    /**
     * 关闭gui事件.
     *
     * @param event 事件
     */
    @EventHandler
    public void onEvent(InventoryCloseEvent event) throws IOException {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof HandyInventory)) {
            return;
        }
        HandyInventory handyInventory = (HandyInventory) holder;
        if (!GuiTypeEnum.VIEW.getType().equals(handyInventory.getGuiType())) {
            return;
        }
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        File file = new File(handyInventory.getSearchType());
        if (!file.exists()) {
            return;
        }
        this.saveViewIndex(inventory, file, handyInventory.getId());
        ConfigUtil.init();
        MessageUtil.sendMessage((Player) humanEntity, BaseUtil.getLangMsg("createMsg"));
    }

    /**
     * 保存view指令中真实变动的index.
     *
     * @param inventory GUI
     * @param file      菜单文件
     * @param size      GUI大小
     */
    private void saveViewIndex(Inventory inventory, File file, int size) throws IOException {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection menuSection = yamlConfiguration.getConfigurationSection("menu");
        Map<String, List<Integer>> currentIndexMap = new LinkedHashMap<>();
        if (menuSection != null) {
            for (String key : menuSection.getKeys(false)) {
                currentIndexMap.put(key, new ArrayList<>());
            }
        }
        Map<Integer, Map<String, Object>> newMenuItemMap = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || Material.AIR.equals(item.getType())) {
                continue;
            }
            String menuKey = this.getMenuKey(item);
            if (StrUtil.isEmpty(menuKey)) {
                newMenuItemMap.put(i, MenuUtil.createMenuItem(item, i));
                continue;
            }
            if (!currentIndexMap.containsKey(menuKey)) {
                newMenuItemMap.put(i, MenuUtil.createMenuItem(item, i));
                continue;
            }
            currentIndexMap.get(menuKey).add(i);
        }
        Map<String, Map<String, Object>> createMenuItemMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<String, Object>> entry : newMenuItemMap.entrySet()) {
            String key = this.getCreateMenuKey(yamlConfiguration, currentIndexMap, createMenuItemMap, entry.getKey());
            createMenuItemMap.put(key, entry.getValue());
        }
        boolean saveFlag = false;
        for (Map.Entry<String, List<Integer>> entry : currentIndexMap.entrySet()) {
            List<Integer> currentIndexList = entry.getValue();
            String menuPath = "menu." + entry.getKey();
            if (CollUtil.isEmpty(currentIndexList)) {
                yamlConfiguration.set(menuPath, null);
                saveFlag = true;
                continue;
            }
            String indexPath = menuPath + ".index";
            List<Integer> oldIndexList = StrUtil.strToIntList(yamlConfiguration.getString(indexPath));
            if (this.isSameIndex(oldIndexList, currentIndexList)) {
                continue;
            }
            yamlConfiguration.set(indexPath, this.joinIndex(currentIndexList));
            saveFlag = true;
        }
        for (Map.Entry<String, Map<String, Object>> entry : createMenuItemMap.entrySet()) {
            yamlConfiguration.set("menu." + entry.getKey(), entry.getValue());
            saveFlag = true;
        }
        if (saveFlag) {
            yamlConfiguration.save(file);
        }
    }

    /**
     * 获取新增菜单物品key.
     *
     * @param yamlConfiguration 菜单配置
     * @param currentIndexMap   当前已有物品槽位
     * @param createMenuItemMap 新增物品
     * @param index             槽位
     * @return 菜单物品key
     */
    private String getCreateMenuKey(YamlConfiguration yamlConfiguration, Map<String, List<Integer>> currentIndexMap,
                                    Map<String, Map<String, Object>> createMenuItemMap, int index) {
        String key = String.valueOf(index);
        if (this.canUseCreateMenuKey(yamlConfiguration, currentIndexMap, createMenuItemMap, key)) {
            return key;
        }
        int next = 1;
        while (!this.canUseCreateMenuKey(yamlConfiguration, currentIndexMap, createMenuItemMap, key + "_" + next)) {
            next++;
        }
        return key + "_" + next;
    }

    /**
     * 判断新增菜单物品key是否可用.
     *
     * @param yamlConfiguration 菜单配置
     * @param currentIndexMap   当前已有物品槽位
     * @param createMenuItemMap 新增物品
     * @param key               菜单物品key
     * @return 是否可用
     */
    private boolean canUseCreateMenuKey(YamlConfiguration yamlConfiguration, Map<String, List<Integer>> currentIndexMap,
                                        Map<String, Map<String, Object>> createMenuItemMap, String key) {
        if (createMenuItemMap.containsKey(key)) {
            return false;
        }
        if (!yamlConfiguration.contains("menu." + key)) {
            return true;
        }
        return currentIndexMap.containsKey(key) && CollUtil.isEmpty(currentIndexMap.get(key));
    }

    /**
     * 获取编辑菜单key.
     *
     * @param itemStack 物品
     * @return 菜单key
     */
    @SuppressWarnings("deprecation")
    private String getMenuKey(ItemStack itemStack) {
        if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_14.getVersionId()) {
            Optional<String> persistentDataOpt = ItemStackUtil.getPersistentData(itemStack, MenuConstants.PREFIX);
            return persistentDataOpt.orElse(null);
        }
        ItemMeta itemMeta = ItemStackUtil.getItemMeta(itemStack);
        List<String> loreList = itemMeta.getLore();
        if (CollUtil.isEmpty(loreList)) {
            return null;
        }
        String keyLore = BaseUtil.stripColor(loreList.get(loreList.size() - 1));
        if (StrUtil.isEmpty(keyLore) || !keyLore.startsWith(MenuConstants.VIEW_KEY_PREFIX)) {
            return null;
        }
        return keyLore.substring(MenuConstants.VIEW_KEY_PREFIX.length());
    }

    /**
     * 判断index是否真实变化.
     *
     * @param oldIndexList 原index
     * @param newIndexList 新index
     * @return 是否一致
     */
    private boolean isSameIndex(List<Integer> oldIndexList, List<Integer> newIndexList) {
        if (oldIndexList.size() != newIndexList.size()) {
            return false;
        }
        return new HashSet<>(oldIndexList).containsAll(newIndexList) && new HashSet<>(newIndexList).containsAll(oldIndexList);
    }

    /**
     * 拼接index配置值.
     *
     * @param indexList index列表
     * @return 配置值
     */
    private String joinIndex(List<Integer> indexList) {
        StringBuilder builder = new StringBuilder();
        for (Integer index : indexList) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(index);
        }
        return builder.toString();
    }

}
