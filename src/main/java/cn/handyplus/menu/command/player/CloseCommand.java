package cn.handyplus.menu.command.player;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 关闭菜单
 *
 * @author handy
 * @since 1.2.1
 */
public class CloseCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "close";
    }

    @Override
    public String permission() {
        return "playerMenu.close";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, ConfigUtil.LANG_CONFIG.getString("noPlayerFailureMsg"));
        player.closeInventory();
    }

}