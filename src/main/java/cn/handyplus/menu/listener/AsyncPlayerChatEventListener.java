package cn.handyplus.menu.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.InputTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.core.MenuCore;
import cn.handyplus.menu.param.MenuButtonParam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 当玩家聊天时触发这个事件
 *
 * @author handy
 */
@HandyListener
public class AsyncPlayerChatEventListener implements Listener {

    /**
     * input处理.
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        MenuButtonParam menuButtonParam = MenuConstants.INPUT_MENU_MAP.get(player.getUniqueId());
        if (menuButtonParam == null) {
            return;
        }
        event.setCancelled(true);
        InputTypeEnum inputTypeEnum = InputTypeEnum.contains(menuButtonParam.getInput());
        String message = event.getMessage();
        // 禁止输入%
        if (message.contains("%")) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noInput"));
            return;
        }
        // 输入T退出
        if ("T".equals(message)) {
            MenuConstants.INPUT_MENU_MAP.remove(player.getUniqueId());
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("exitInput", "&8[&a✔&8] &7成功退出输入"));
            return;
        }
        // 数字校验
        if (InputTypeEnum.NUMBER.equals(inputTypeEnum)) {
            Optional<BigDecimal> numericOpt = NumberUtil.isNumericToBigDecimal(message);
            if (!numericOpt.isPresent()) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noNumber"));
                return;
            }
        }
        MenuConstants.PLAYER_INPUT_MAP.put(player.getUniqueId(), message);
        MenuConstants.INPUT_MENU_MAP.remove(player.getUniqueId());
        // 继续执行菜单逻辑
        HandySchedulerUtil.runTask(() -> MenuCore.executeMenu(player, menuButtonParam));
    }

}