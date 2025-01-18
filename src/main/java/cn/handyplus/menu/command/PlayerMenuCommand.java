package cn.handyplus.menu.command;

import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.command.HandyCommandWrapper;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.TabListEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 主命令
 *
 * @author handy
 */
@HandyCommand(name = "playerMenu")
public class PlayerMenuCommand implements TabExecutor {
    private final static String PERMISSION = "playerMenu.reload";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // 判断指令是否正确
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }
        boolean rst = HandyCommandWrapper.onCommand(sender, cmd, label, args, BaseUtil.getMsgNotColor("noPermission"));
        if (!rst) {
            sendHelp(sender);
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands;
        commands = TabListEnum.returnList(args, args.length);
        if (commands == null) {
            return null;
        }
        StringUtil.copyPartialMatches(args[args.length - 1].toLowerCase(), commands, completions);
        Collections.sort(completions);
        return completions;
    }

    /**
     * 发送帮助
     *
     * @param sender 发送人
     */
    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION)) {
            return;
        }
        List<String> helps = BaseConstants.LANG_CONFIG.getStringList("helps");
        for (String help : helps) {
            MessageUtil.sendMessage(sender, help);
        }
        return;
    }

}
