package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.inventory.CreateGui;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, ConfigUtil.LANG_CONFIG.getString("noPlayerFailureMsg"));
        Integer size = BaseConstants.GUI_SIZE_54;
        if (args.length > 1) {
            size = AssertUtil.isNumericToInt(args[1], sender, ConfigUtil.LANG_CONFIG.getString("noSize"));
            if (!MenuConstants.GUI_SIZE.contains(size)) {
                MessageApi.sendMessage(player, ConfigUtil.LANG_CONFIG.getString("noSize"));
                return;
            }
        }
        player.openInventory(CreateGui.getInstance().createGui(player, size));
    }

}
