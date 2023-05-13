package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import com.handy.guild.api.PlayerGuildApi;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * 物品打开菜单
 *
 * @author handy
 */
@HandyListener
public class PlayerInteractEventListener implements Listener {

    /**
     * 当玩家对一个对象或空气进行交互时触发本事件..
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }
        Player player = event.getPlayer();
        // 判断是否开启唯一菜单
        String clockMenu = ConfigUtil.CONFIG.getString("clock.menu", "");
        boolean enable = ConfigUtil.CONFIG.getBoolean("clock.enable");
        if (enable && StrUtil.isNotEmpty(clockMenu)) {
            String actionStr = ConfigUtil.CONFIG.getString("clock.action", "ALL");
            Action action = event.getAction();
            switch (actionStr.toUpperCase()) {
                case "ALL":
                    break;
                case "LEFT":
                    // 不是左键处理
                    if (!Action.LEFT_CLICK_AIR.equals(action) && !Action.LEFT_CLICK_BLOCK.equals(action)) {
                        return;
                    }
                    break;
                case "RIGHT":
                    // 不是右键处理
                    if (!Action.RIGHT_CLICK_AIR.equals(action) && !Action.RIGHT_CLICK_BLOCK.equals(action)) {
                        return;
                    }
                    break;
                default:
                    return;
            }
            // 物品不对处理
            if (!ItemStackUtil.isSimilar(item, MenuUtil.getClock())) {
                return;
            }
        } else {
            // 不是唯一菜单处理
            String openItem = item.getType().name().toLowerCase(Locale.ROOT);
            String menu = ConfigUtil.ITEM_MAP.get(openItem);
            if (StrUtil.isEmpty(menu)) {
                return;
            }
            clockMenu = menu;
        }
        // 判断是否在公会战
        if (PlayerMenu.USE_GUILD && PlayerGuildApi.getInstance().isPvp(player)) {
            MessageApi.sendMessage(player, BaseUtil.getLangMsg("noOpenPvpPermission"));
            return;
        }
        MenuUtil.openGui(player, clockMenu);
    }

}