package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * 管理打开菜单
 *
 * @author handy
 */
public class AdminOpenCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "adminOpen";
    }

    @Override
    public String permission() {
        return "playerMenu.adminOpen";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 3, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        String playerName = args[2];
        Optional<Player> onlinePlayerOpt = BaseUtil.getOnlinePlayer(playerName);
        if (!onlinePlayerOpt.isPresent()) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("playerNotOnline"));
            return;
        }
        // 是否为玩家
        Player player = onlinePlayerOpt.get();
        MenuUtil.asyncOpenGui(player, args[1], args.length > 3 ? args[3] : null);
    }

}