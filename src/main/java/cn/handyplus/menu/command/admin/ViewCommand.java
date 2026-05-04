package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.HandyTab;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.YmlUtil;
import cn.handyplus.lib.internal.PlayerSchedulerUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.inventory.ViewGui;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * 编辑菜单
 *
 * @author handy
 */
public class ViewCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "view";
    }

    @Override
    public String permission() {
        return "playerMenu.view";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void tab(HandyTab handyTab) {
        handyTab.next(n -> new ArrayList<>(ConfigUtil.MENU_CONFIG_MAP.keySet()));
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, BaseUtil.getLangMsg("paramFailureMsg"));
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        String menu = YmlUtil.setYml(args[1]);
        Inventory inventory = ViewGui.getInstance().createGui(player, menu);
        if (inventory == null) {
            MessageUtil.sendMessage(player, BaseUtil.getLangMsg("noMenu", "").replace("${menu}", args[1]));
            return;
        }
        PlayerSchedulerUtil.openInventory(player, inventory);
    }

}
