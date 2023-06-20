package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.YmlUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.inventory.ViewGui;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

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
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_14.getVersionId()) {
            MessageApi.sendMessage(sender, ConfigUtil.LANG_CONFIG.getString("versionFailureMsg", "&8[&c✘&8] &4服务端版本小于1.14,无法使用该指令"));
            return;
        }
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, ConfigUtil.LANG_CONFIG.getString("paramFailureMsg"));
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, ConfigUtil.LANG_CONFIG.getString("noPlayerFailureMsg"));
        String menu = YmlUtil.setYml(args[1]);
        new BukkitRunnable() {
            @Override
            public void run() {
                Inventory inventory = ViewGui.getInstance().createGui(player, menu);
                if (inventory == null) {
                    MessageApi.sendMessage(player, ConfigUtil.LANG_CONFIG.getString("noMenu", "").replace("${menu}", args[1]));
                    return;
                }
                Bukkit.getScheduler().runTask(PlayerMenu.getInstance(), () -> player.openInventory(inventory));
            }
        }.runTaskAsynchronously(PlayerMenu.getInstance());
    }

}
