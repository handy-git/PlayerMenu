package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.Material;
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
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 关闭编辑菜单保存数据
 *
 * @author handy
 */
@HandyListener
public class MenuCreateEventListener implements Listener {

    /**
     * 关闭gui事件
     *
     * @param event 事件
     */
    @EventHandler
    public void onEvent(InventoryCloseEvent event) throws IOException {
        // 校验
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof HandyInventory)) {
            return;
        }
        HandyInventory handyInventory = (HandyInventory) holder;
        if (!GuiTypeEnum.CREATE.getType().equals(handyInventory.getGuiType())) {
            return;
        }
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player) humanEntity;
        int size = handyInventory.getId();

        Map<String, Map<String, Object>> createMenuItemMap = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || Material.AIR.equals(item.getType())) {
                continue;
            }
            createMenuItemMap.put(String.valueOf(i), MenuUtil.createMenuItem(item, i));
        }
        String fileName = handyInventory.getSearchType();
        File file = new File(fileName);
        if (!file.exists()) {
            boolean mkdir = file.createNewFile();
            file = new File(fileName);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (StrUtil.isEmpty(yamlConfiguration.getString("title"))) {
            yamlConfiguration.set("title", "&7Title");
        }
        if (StrUtil.isEmpty(yamlConfiguration.getString("openCommand"))) {
            yamlConfiguration.set("openCommand", "");
        }
        if (StrUtil.isEmpty(yamlConfiguration.getString("openItem"))) {
            yamlConfiguration.set("openItem", "");
        }
        if (StrUtil.isEmpty(yamlConfiguration.getString("permission"))) {
            yamlConfiguration.set("permission", true);
        }
        if (StrUtil.isEmpty(yamlConfiguration.getString("sound"))) {
            yamlConfiguration.set("sound", "");
        }
        yamlConfiguration.set("size", size);
        yamlConfiguration.set("menu", createMenuItemMap);
        yamlConfiguration.save(new File(fileName));
        ConfigUtil.init();
        MessageUtil.sendMessage(player, BaseUtil.getLangMsg("createMsg"));
    }

}
