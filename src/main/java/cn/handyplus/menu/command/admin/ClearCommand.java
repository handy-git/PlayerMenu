package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.service.MenuLimitService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * 清理数据
 *
 * @author handy
 */
public class ClearCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "clear";
    }

    @Override
    public String permission() {
        return "playerMenu.clear";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        // 获取要清理的数据ID
        List<Integer> menuItemIds = StrUtil.strToIntList(args[1]);
        // 开始清理
        MenuLimitService.getInstance().deleteByMenuItemIds(menuItemIds);
        // 发送提醒
        String clearSucceedMsg = BaseUtil.getMsgNotColor("clearSucceedMsg", MapUtil.of("${id}", menuItemIds.toString()));
        MessageUtil.sendMessage(sender, clearSucceedMsg);
    }

}
