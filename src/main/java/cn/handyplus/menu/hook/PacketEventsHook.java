package cn.handyplus.menu.hook;

import cn.handyplus.menu.core.HideInventoryUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 PacketEvents 隐藏玩家菜单打开期间的背包物品.
 *
 * @author handy
 */
public class PacketEventsHook extends PacketListenerAbstract {
    private static PacketEventsHook instance;

    /**
     * 注册 PacketEvents 数据包监听器.
     */
    public static void enable() {
        if (instance != null) {
            return;
        }
        instance = new PacketEventsHook();
        PacketEvents.getAPI().getEventManager().registerListener(instance);
    }

    /**
     * 移除 PacketEvents 数据包监听器.
     */
    public static void disable() {
        if (instance == null) {
            HideInventoryUtil.clear();
            return;
        }
        PacketEvents.getAPI().getEventManager().unregisterListener(instance);
        instance = null;
        HideInventoryUtil.clear();
    }

    /**
     * 创建数据包监听器.
     */
    private PacketEventsHook() {
        super(PacketListenerPriority.HIGHEST);
    }

    /**
     * 改写玩家菜单打开期间发往客户端的背包数据.
     *
     * @param event 数据包事件
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Integer topSize = HideInventoryUtil.getTopSize(player.getUniqueId());
        if (topSize == null) {
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            this.hideSetSlot(event, topSize);
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            this.hideWindowItems(event, topSize);
        }
    }

    /**
     * 隐藏单槽位数据包中的玩家背包物品.
     *
     * @param event   数据包事件
     * @param topSize 顶部容器大小
     */
    private void hideSetSlot(PacketSendEvent event, int topSize) {
        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
        int windowId = packet.getWindowId();
        int slot = packet.getSlot();
        // 玩家背包窗口使用 9-44；菜单窗口从顶部容器大小之后开始计算。
        boolean playerInventorySlot = (windowId == 0 || windowId == -2)
                ? slot >= 9 && slot <= 44
                : windowId > 0 && slot >= topSize && slot < topSize + 36;
        if (playerInventorySlot) {
            packet.setItem(ItemStack.EMPTY);
            event.markForReEncode(true);
        }
    }

    /**
     * 隐藏整组槽位数据包中的玩家背包物品.
     *
     * @param event   数据包事件
     * @param topSize 顶部容器大小
     */
    private void hideWindowItems(PacketSendEvent event, int topSize) {
        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
        List<ItemStack> items = packet.getItems();
        List<ItemStack> hiddenItems = new ArrayList<>(items);
        // windowId 为 0/-2 时是玩家背包；其他窗口从顶部容器大小之后开始计算。
        int start = packet.getWindowId() == 0 || packet.getWindowId() == -2 ? 9 : topSize;
        int end = Math.min(start + 36, hiddenItems.size());
        for (int index = start; index < end; index++) {
            hiddenItems.set(index, ItemStack.EMPTY);
        }
        packet.setItems(hiddenItems);
        event.markForReEncode(true);
    }

}
