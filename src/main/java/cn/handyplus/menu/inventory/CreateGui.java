package cn.handyplus.menu.inventory;

import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.GuiTypeEnum;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * 创建菜单
 *
 * @author handy
 */
public class CreateGui {

    private CreateGui() {
    }

    private final static CreateGui INSTANCE = new CreateGui();

    public static CreateGui getInstance() {
        return INSTANCE;
    }

    /**
     * 创建gui
     *
     * @param player 玩家
     * @param size   大小
     * @return gui
     */
    public Inventory createGui(Player player, Integer size) {
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.CREATE.getType(), GuiTypeEnum.CREATE.getTitle(), size);
        // 设置数据
        handyInventory.setPlayer(player);
        handyInventory.setId(size);
        handyInventory.setToCancel(false);
        handyInventory.setSearchType(PlayerMenu.INSTANCE.getDataFolder() + "/menu/" + System.currentTimeMillis() + ".yml");
        return handyInventory.getInventory();
    }

}