package cn.handyplus.menu.command.player;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 打开菜单
 *
 * @author handy
 */
public class OpenCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "open";
    }

    @Override
    public String permission() {
        return "playerMenu.open";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        MenuUtil.openGui(player, args[1], args.length > 2 ? args[2] : null);
    }

}