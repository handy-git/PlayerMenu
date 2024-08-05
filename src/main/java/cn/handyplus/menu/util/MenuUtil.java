package cn.handyplus.menu.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.DateUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.core.YmlUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.inventory.MenuGui;
import cn.handyplus.menu.service.MenuLimitService;
import com.handy.guild.api.PlayerGuildApi;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        openGui(player, menu, null);
    }

    /**
     * 打开菜单
     *
     * @param player   玩家
     * @param menu     菜单
     * @param papiName 变量玩家
     * @since 1.3.0
     */
    public static void openGui(Player player, String menu, String papiName) {
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            // 判断是否在公会战
            if (PlayerMenu.USE_GUILD && PlayerGuildApi.getInstance().isPvp(player)) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noOpenPvpPermission"));
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
            Inventory inventory = MenuGui.getInstance().createGui(player, finalMenu, papiName);
            if (inventory == null) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMenu", "").replace("${menu}", menu));
                return;
            }
            // 打开菜单
            HandySchedulerUtil.runTask(() -> player.openInventory(inventory));
        });
    }

    /**
     * 播放声音
     *
     * @param player   玩家
     * @param soundStr 声音
     */
    public static void playSound(Player player, String soundStr) {
        if (StrUtil.isEmpty(soundStr)) {
            return;
        }
        Optional<Sound> soundOpt = BaseUtil.getSound(soundStr);
        if (!soundOpt.isPresent()) {
            MessageUtil.sendMessage(player, "没有 " + soundStr + " 音效");
            return;
        }
        PlayerSchedulerUtil.playSound(player, soundOpt.get(), 1, 1);
    }

    /**
     * 点击CD判断
     *
     * @param player     玩家
     * @param menuItemId 菜单id
     * @param cd         冷却
     * @return true 不满足
     */
    public static boolean clickCd(Player player, Integer menuItemId, int cd) {
        if (cd <= 0 || menuItemId == null) {
            return false;
        }
        Date clickTime = MenuLimitService.getInstance().findTimeByPlayerUuid(player.getUniqueId(), menuItemId);
        if (clickTime != null) {
            long time = DateUtil.offset(clickTime, Calendar.SECOND, cd).getTime() - System.currentTimeMillis();
            if (time > 0) {
                String noTimeLimit = BaseUtil.getMsgNotColor("noTimeLimit", "");
                MessageUtil.sendMessage(player, StrUtil.replace(noTimeLimit, "time", String.valueOf(time / 1000)));
                return true;
            }
        }
        return false;
    }

    /**
     * 点击次数判断
     *
     * @param player     玩家
     * @param menuItemId 菜单id
     * @param limit      次数
     * @return true 不满足
     */
    public static boolean clickLimit(Player player, Integer menuItemId, int limit) {
        if (limit <= 0 || menuItemId == null) {
            return false;
        }
        Integer count = MenuLimitService.getInstance().findCountByPlayerUuid(player.getUniqueId(), menuItemId);
        if (count >= limit) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noLimit"));
            return true;
        }
        return false;
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
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noOpenPermission", "").replace("${permission}", openPermission));
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
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noOpenWorldPermission"));
            return false;
        }
        return true;
    }

}