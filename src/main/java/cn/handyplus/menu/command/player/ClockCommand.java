package cn.handyplus.menu.command.player;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.util.MenuUtil;
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
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = MenuUtil.getClock();
        for (ItemStack stack : inventory) {
            if (ItemStackUtil.isSimilar(itemStack, stack)) {
                MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("clockFailureMsg"));
                return;
            }
        }
        ItemStackUtil.addItem(player, itemStack, BaseUtil.getMsgNotColor("addItemMsg"));
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("clockSucceedMsg"));
    }

}