package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.util.ConfigUtil;
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

import java.io.File;
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
    public void onEvent(InventoryCloseEvent event) {
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
    private void saveViewIndex(Inventory inventory, File file, int size) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection menuSection = yamlConfiguration.getConfigurationSection("menu");
        if (menuSection == null) {
            return;
        }
        Map<String, List<Integer>> currentIndexMap = new LinkedHashMap<>();
        for (String key : menuSection.getKeys(false)) {
            currentIndexMap.put(key, new ArrayList<>());
        }
        for (int i = 0; i < size; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || Material.AIR.equals(item.getType())) {
                continue;
            }
            Optional<String> persistentDataOpt = ItemStackUtil.getPersistentData(item, MenuConstants.PREFIX);
            if (!persistentDataOpt.isPresent()) {
                continue;
            }
            String menuKey = persistentDataOpt.get();
            if (StrUtil.isEmpty(menuKey) || !currentIndexMap.containsKey(menuKey)) {
                continue;
            }
            currentIndexMap.get(menuKey).add(i);
        }
        String child = "menu/" + file.getName();
        for (Map.Entry<String, List<Integer>> entry : currentIndexMap.entrySet()) {
            List<Integer> currentIndexList = entry.getValue();
            if (CollUtil.isEmpty(currentIndexList)) {
                continue;
            }
            String indexPath = "menu." + entry.getKey() + ".index";
            List<Integer> oldIndexList = StrUtil.strToIntList(yamlConfiguration.getString(indexPath));
            if (this.isSameIndex(oldIndexList, currentIndexList)) {
                continue;
            }
            HandyConfigUtil.setPath(yamlConfiguration, indexPath, this.joinIndex(currentIndexList), child);
        }
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
