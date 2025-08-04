package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.inventory.CreateGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * 创建菜单
 *
 * @author handy
 */
public class CreateCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "create";
    }

    @Override
    public String permission() {
        return "playerMenu.create";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        Integer size = BaseConstants.GUI_SIZE_54;
        if (args.length > 1) {
            size = AssertUtil.isNumericToInt(args[1], BaseUtil.getMsgNotColor("noSize"));
            if (!MenuConstants.GUI_SIZE.contains(size)) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noSize"));
                return;
            }
        }
        Inventory inventory = CreateGui.getInstance().createGui(player, size);
        PlayerSchedulerUtil.syncOpenInventory(player, inventory);
    }

}
