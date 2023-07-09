package cn.handyplus.menu.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.YmlUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.inventory.MenuGui;
import com.handy.guild.api.PlayerGuildApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * 工具类
 *
 * @author handy
 */
public class MenuUtil {

    /**
     * 获取唯一菜单
     *
     * @return 菜单
     */
    public static ItemStack getClock() {
        String material = ConfigUtil.CONFIG.getString("clock.material");
        String name = ConfigUtil.CONFIG.getString("clock.name");
        List<String> loreList = ConfigUtil.CONFIG.getStringList("clock.lore");
        boolean isEnchant = ConfigUtil.CONFIG.getBoolean("clock.isEnchant");
        int customModelDataId = ConfigUtil.CONFIG.getInt("clock.custom-model-data");
        return ItemStackUtil.getItemStack(material, name, loreList, isEnchant, customModelDataId);
    }

    /**
     * 打开菜单
     *
     * @param player 玩家
     * @param menu   菜单
     */
    public static void openGui(Player player, String menu) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 判断是否在公会战
                if (PlayerMenu.USE_GUILD && PlayerGuildApi.getInstance().isPvp(player)) {
                    MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noOpenPvpPermission"));
                    return;
                }
                String finalMenu = YmlUtil.setYml(menu);
                // 校验权限
                if (!checkPermission(player, finalMenu)) {
                    return;
                }
                // 禁止对应世界打开
                if (!checkWorld(player)) {
                    return;
                }
                // 生成菜单
                Inventory inventory = MenuGui.getInstance().createGui(player, finalMenu);
                if (inventory == null) {
                    MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noMenu", "").replace("${menu}", menu));
                    return;
                }
                // 打开菜单
                Bukkit.getScheduler().runTask(PlayerMenu.getInstance(), () -> player.openInventory(inventory));
            }
        }.runTaskAsynchronously(PlayerMenu.getInstance());
    }

    /**
     * 校验权限
     *
     * @param player    玩家
     * @param finalMenu 菜单
     * @return true 有权限操作
     */
    private static boolean checkPermission(Player player, String finalMenu) {
        if (!ConfigUtil.PERMISSION_MAP.getOrDefault(finalMenu, true)) {
            return true;
        }
        String openPermission = "playerMenu.open." + finalMenu;
        if (!player.hasPermission(openPermission)) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noOpenPermission", "").replace("${permission}", openPermission));
            return false;
        }
        return true;
    }

    /**
     * 校验世界
     *
     * @param player 玩家
     */
    private static boolean checkWorld(Player player) {
        List<String> noWorld = ConfigUtil.CONFIG.getStringList("noWorld");
        if (CollUtil.isNotEmpty(noWorld) && noWorld.contains(player.getWorld().getName())) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noOpenWorldPermission"));
            return false;
        }
        return true;
    }

}