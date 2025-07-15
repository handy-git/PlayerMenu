package cn.handyplus.menu.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.DbTypeEnum;
import cn.handyplus.lib.db.SqlManagerUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.enter.MenuLimit;
import cn.handyplus.menu.service.MenuItemService;
import cn.handyplus.menu.service.MenuLimitService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

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
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        String storageMethod = args[1];
        if (!DbTypeEnum.MySQL.getType().equalsIgnoreCase(storageMethod) && !DbTypeEnum.SQLite.getType().equalsIgnoreCase(storageMethod)) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
            return;
        }
        if (storageMethod.equalsIgnoreCase(BaseConstants.STORAGE_CONFIG.getString(SqlManagerUtil.STORAGE_METHOD))) {
            MessageUtil.sendMessage(sender, "&4禁止转换！原因，您当前使用的存储方式已经为：" + storageMethod);
            return;
        }
        // 查询当前全部数据
        List<MenuItem> all = MenuItemService.getInstance().findAll();
        List<MenuLimit> all1 = MenuLimitService.getInstance().findAll();
        // 修改链接方式
        HandyConfigUtil.setPath(BaseConstants.STORAGE_CONFIG, "storage-method", storageMethod, Collections.singletonList("存储方法(MySQL,SQLite)请复制括号内的类型,不要自己写"), "storage.yml");
        // 加载新连接
        SqlManagerUtil.getInstance().enableSql();
        // 新连接创建表
        Db.use(MenuItem.class).createTable();
        Db.use(MenuLimit.class).createTable();
        // 插入数据
        Db.use(MenuItem.class).execution().insertBatch(all);
        Db.use(MenuLimit.class).execution().insertBatch(all1);
        MessageUtil.sendMessage(sender, "&4转换数据完成，请务必重启服务器，不然有可能会出现未知bug");
    }

}