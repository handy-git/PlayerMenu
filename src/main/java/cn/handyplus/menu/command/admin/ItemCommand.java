package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.inventory.ItemGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * 打开物品库菜单
 *
 * @author handy
 * @since 1.7.7
 */
public class ItemCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "item";
    }

    @Override
    public String permission() {
        return "playerMenu.item";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        // 创建gui
        Inventory inventory = ItemGui.getInstance().createGui(player);
        HandySchedulerUtil.runTask(() -> player.openInventory(inventory));
    }

}