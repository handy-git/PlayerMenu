package cn.handyplus.menu.listener.gui;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.constants.InputTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.core.MenuCore;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

/**
 * 菜单功能处理
 *
 * @author handy
 */
public class InventoryClickEventListener implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.MENU.getType();
    }

    @Override
    public void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        Player player = handyInventory.getPlayer();
        Map<Integer, Object> listMap = handyInventory.getObjMap();
        Object obj = listMap.get(rawSlot);
        if (!(obj instanceof MenuButtonParam)) {
            return;
        }
        MenuButtonParam menuButtonParam = (MenuButtonParam) obj;
        // 检查点击条件是否满足
        if (MenuCore.check(player, menuButtonParam)) {
            // 播放未满足条件的声音
            MenuUtil.playSound(player, StrUtil.isNotEmpty(menuButtonParam.getFailSound()) ? menuButtonParam.getFailSound() : menuButtonParam.getSound());
            return;
        }
        // 检查是否需要进行输入
        if (StrUtil.isNotEmpty(menuButtonParam.getInput())) {
            MenuConstants.INPUT_MENU_MAP.put(player.getUniqueId(), menuButtonParam);
            InputTypeEnum inputTypeEnum = InputTypeEnum.contains(menuButtonParam.getInput());
            MessageUtil.sendMessage(player, InputTypeEnum.replaceFirst(menuButtonParam.getInput(), inputTypeEnum));
            player.closeInventory();
            return;
        }
        // 菜单执行
        MenuCore.executeMenu(handyInventory, player, menuButtonParam);
    }

}