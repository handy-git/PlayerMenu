package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.HandyTab;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.db.DbTypeEnum;
import cn.handyplus.lib.db.SqlConvertUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * 转换数据-从mysql转换到sqlite，或者反之
 *
 * @author handy
 * @since 1.6.6
 */
public class ConvertCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "convert";
    }

    @Override
    public String permission() {
        return "playerMenu.convert";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void tab(HandyTab handyTab) {
        handyTab.next(DbTypeEnum.getEnum());
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, BaseUtil.getLangMsg("paramFailureMsg"));
        String storageMethod = args[1];
        SqlConvertUtil.convert(storageMethod);
    }

}