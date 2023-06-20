package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 重载配置
 *
 * @author handy
 */
public class ReloadCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "reload";
    }

    @Override
    public String permission() {
        return "playerMenu.reload";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigUtil.init();
                MessageApi.sendMessage(sender, ConfigUtil.LANG_CONFIG.getString("reloadMsg"));
            }
        }.runTaskAsynchronously(PlayerMenu.getInstance());
    }

}