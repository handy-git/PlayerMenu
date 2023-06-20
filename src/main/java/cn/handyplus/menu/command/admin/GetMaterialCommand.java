package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 获取材质
 *
 * @author handy
 */
public class GetMaterialCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "getMaterial";
    }

    @Override
    public String permission() {
        return "playerMenu.getMaterial";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, ConfigUtil.LANG_CONFIG.getString("noPlayerFailureMsg"));
        // 物品
        ItemStack itemInMainHand = ItemStackUtil.getItemInMainHand(player.getInventory());
        String name = itemInMainHand.getType().name();
        MessageApi.sendMessage(sender, name);
        MessageApi.sendConsoleMessage(name);
    }

}