package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.HandyTab;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.internal.PlayerSchedulerUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.inventory.CreateGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

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
    public void tab(HandyTab handyTab) {
        handyTab.next(Arrays.asList("9", "18", "27", "36", "45", "54"));
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        Integer size = BaseConstants.GUI_SIZE_54;
        if (args.length > 1) {
            size = AssertUtil.isNumericToInt(args[1], BaseUtil.getLangMsg("noSize"));
            if (!MenuConstants.GUI_SIZE.contains(size)) {
                MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noSize"));
                return;
            }
        }
        Inventory inventory = CreateGui.getInstance().createGui(player, size);
        PlayerSchedulerUtil.openInventory(player, inventory);
    }

}
