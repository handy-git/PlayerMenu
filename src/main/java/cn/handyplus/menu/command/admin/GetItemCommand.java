package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.service.MenuItemService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 获取物品
 *
 * @author handy
 */
public class GetItemCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "getItem";
    }

    @Override
    public String permission() {
        return "playerMenu.getItem";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        Integer id = AssertUtil.isNumericToInt(args[1], sender, BaseUtil.getMsgNotColor("getMenuItemMsg"));
        Optional<MenuItem> menuItem = MenuItemService.getInstance().findById(id);
        if (!menuItem.isPresent()) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("getMenuItemMsg"));
            return;
        }
        ItemStack itemStack = ItemStackUtil.itemStackDeserialize(menuItem.get().getItemStack());
        int number = 1;
        if (args.length > 2) {
            number = AssertUtil.isNumericToInt(args[2], sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        }
        ItemStackUtil.addItem(player, itemStack, number);
    }

}