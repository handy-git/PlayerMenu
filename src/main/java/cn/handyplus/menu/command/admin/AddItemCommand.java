package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.MapUtil;
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

/**
 * 新增物品
 *
 * @author handy
 */
public class AddItemCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "addItem";
    }

    @Override
    public String permission() {
        return "playerMenu.addItem";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        // 物品
        ItemStack itemInMainHand = ItemStackUtil.getItemInMainHand(player.getInventory());
        ItemStack clone = itemInMainHand.clone();
        clone.setAmount(1);
        MenuItem menuItem = new MenuItem();
        menuItem.setItemStack(ItemStackUtil.itemStackSerialize(clone));
        int id = MenuItemService.getInstance().add(menuItem);
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("addMenuItemMsg", MapUtil.of("${id}", id + "")));
    }

}