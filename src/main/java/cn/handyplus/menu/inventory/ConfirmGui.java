package cn.handyplus.menu.inventory;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * 确认
 *
 * @author handy
 */
public class ConfirmGui {
    private ConfirmGui() {
    }

    private final static ConfirmGui INSTANCE = new ConfirmGui();

    public static ConfirmGui getInstance() {
        return INSTANCE;
    }

    /**
     * 创建gui
     *
     * @param player 玩家
     * @param param  菜单参数
     * @return gui
     */
    public Inventory createGui(@NotNull Player player, @NotNull MenuButtonParam param) {
        String title = ConfigUtil.CONFIRM_CONFIG.getString("title");
        String sound = ConfigUtil.CONFIRM_CONFIG.getString("sound");
        int size = ConfigUtil.CONFIRM_CONFIG.getInt("size", BaseConstants.GUI_SIZE_27);
        title = PlaceholderApiUtil.set(player, title);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.CONFIRM.getType(), title, size, sound);
        // 设置数据
        handyInventory.setPlayer(player);
        handyInventory.setObj(param);
        handyInventory.setPlayer(player);
        handyInventory.setStrMap(MapUtil.of(0, param.getSourceMenu().getKey()));
        handyInventory.setSearchType(param.getSourceMenu().getValue());
        this.setInventoryDate(handyInventory);
        return handyInventory.getInventory();
    }

    /**
     * 设置数据
     *
     * @param handyInventory gui
     */
    private void setInventoryDate(HandyInventory handyInventory) {
        // 基础设置
        handyInventory.setGuiType(GuiTypeEnum.CONFIRM.getType());
        // 1. 刷新
        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());
        // 2.设置功能性菜单
        this.setFunctionMenu(handyInventory);
    }

    /**
     * 设置功能性菜单
     *
     * @param handyInventory GUI
     */
    private void setFunctionMenu(HandyInventory handyInventory) {
        // 确定
        HandyInventoryUtil.setButton(ConfigUtil.CONFIRM_CONFIG, handyInventory, "confirm");
        // 取消
        HandyInventoryUtil.setButton(ConfigUtil.CONFIRM_CONFIG, handyInventory, "cancel");
        // 返回按钮
        HandyInventoryUtil.setButton(ConfigUtil.CONFIRM_CONFIG, handyInventory, "back");
        // 分隔板
        HandyInventoryUtil.setButton(ConfigUtil.CONFIRM_CONFIG, handyInventory, "pane");
    }

}
