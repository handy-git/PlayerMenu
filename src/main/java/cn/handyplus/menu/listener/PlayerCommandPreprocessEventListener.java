package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import com.handy.guild.api.PlayerGuildApi;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * 命令打开菜单
 *
 * @author handy
 */
@HandyListener
public class PlayerCommandPreprocessEventListener implements Listener {

    /**
     * 当一个玩家执行一个命令的时候将会被触发
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String menu = ConfigUtil.COMMAND_MAP.get(event.getMessage().replace("/", ""));
        if (StrUtil.isEmpty(menu)) {
            return;
        }
        Player player = event.getPlayer();
        // 判断是否在公会战
        if (PlayerMenu.USE_GUILD && PlayerGuildApi.getInstance().isPvp(player)) {
            MessageApi.sendMessage(player, BaseUtil.getLangMsg("noOpenPvpPermission"));
            return;
        }
        MenuUtil.openGui(player, menu);
        event.setCancelled(true);
    }

}
