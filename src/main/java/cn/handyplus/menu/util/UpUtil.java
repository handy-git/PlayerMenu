package cn.handyplus.menu.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.enter.MenuItem;
import cn.handyplus.menu.service.MenuItemService;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 升级版本专用
 *
 * @author handy
 * @since 1.6.6
 */
public class UpUtil {

    /**
     * 初始化方法
     */
    public synchronized static void up() {
        HandySchedulerUtil.runTaskLaterAsynchronously(() -> {
            String version = Bukkit.getVersion();
            String nowVersion = BaseConstants.CONFIG.getString("version");
            // 版本一致 跳过
            if (version.equals(nowVersion)) {
                return;
            }
            // 重设版本号
            HandyConfigUtil.setPath(BaseConstants.CONFIG, "version", version, null, "config.yml");
            // 初始化
            step1();
            // 打印提醒
            if (StrUtil.isNotEmpty(nowVersion)) {
                MessageUtil.sendConsoleMessage("版本变动,进行数据升级,当前版本:" + nowVersion + ",升级版本:" + version);
            }
        }, 20 * 10);
    }

    private static void step1() {
        List<MenuItem> menuItemList = MenuItemService.getInstance().findAll();
        if (CollUtil.isEmpty(menuItemList)) {
            return;
        }
        for (MenuItem menuItem : menuItemList) {
            ItemStack itemStack = ItemStackUtil.itemStackDeserialize(menuItem.getItemStack());
            String itemStackStr = ItemStackUtil.itemStackSerialize(itemStack);
            MenuItemService.getInstance().updateItemStack(itemStackStr, menuItem.getId());
        }
    }

}