package cn.handyplus.menu.command.player;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.MenuConstants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author handy
 */
public class ClockCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "clock";
    }

    @Override
    public String permission() {
        return "playerMenu.clock";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = MenuConstants.CLOCK;
        for (ItemStack stack : inventory) {
            if (ItemStackUtil.isSimilar(itemStack, stack)) {
                MessageUtil.sendMessage(sender, BaseUtil.getLangMsg("clockFailureMsg"));
                return;
            }
        }
        ItemStackUtil.addItem(player, itemStack, BaseUtil.getLangMsg("addItemMsg"));
        MessageUtil.sendMessage(sender, BaseUtil.getLangMsg("clockSucceedMsg"));
    }

}