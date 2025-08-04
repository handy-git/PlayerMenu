package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.YmlUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.inventory.ViewGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_14.getVersionId()) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("versionFailureMsg", "&8[&c✘&8] &4服务端版本小于1.14,无法使用该指令"));
            return;
        }
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, BaseUtil.getMsgNotColor("paramFailureMsg"));
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        String menu = YmlUtil.setYml(args[1]);
        Inventory inventory = ViewGui.getInstance().createGui(player, menu);
        if (inventory == null) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMenu", "").replace("${menu}", args[1]));
            return;
        }
        PlayerSchedulerUtil.syncOpenInventory(player, inventory);
    }

}
