package cn.handyplus.menu.hook;

import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.core.HideInventoryUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家菜单背包物品隐藏.
 *
 * @author handy
 */
public class ProtocolLibHook extends PacketAdapter {
    private static ProtocolLibHook instance;

    /**
     * 注册 ProtocolLib 数据包监听器.
     */
    public static void enable() {
        if (instance != null) {
            return;
        }
        instance = new ProtocolLibHook();
        ProtocolLibrary.getProtocolManager().addPacketListener(instance);
    }

    /**
     * 移除 ProtocolLib 数据包监听器.
     */
    public static void disable() {
        if (instance == null) {
            HideInventoryUtil.clear();
            return;
        }
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListener(instance);
        instance = null;
        HideInventoryUtil.clear();
    }

    /**
     * 创建数据包监听器.
     */
    private ProtocolLibHook() {
        super(PlayerMenu.INSTANCE, ListenerPriority.HIGHEST,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS
        );
    }

    /**
     * 改写玩家菜单打开期间发往客户端的背包数据.
     *
     * @param event 数据包事件
     */
    @Override
    public void onPacketSending(PacketEvent event) {
        // 只改写当前玩家菜单底部的玩家背包槽位，顶部菜单内容保持不变。
        Integer topSize = HideInventoryUtil.getTopSize(event.getPlayer().getUniqueId());
        if (topSize == null) {
            return;
        }
        PacketContainer packet = event.getPacket();
        int windowId = packet.getIntegers().read(0);
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            this.hideSetSlot(packet, windowId, topSize);
            return;
        }
        this.hideWindowItems(packet, windowId, topSize);
    }

    /**
     * 隐藏单槽位数据包中的玩家背包物品.
     *
     * @param packet 数据包
     * @param windowId 窗口 ID
     * @param topSize 顶部容器大小
     */
    private void hideSetSlot(PacketContainer packet, int windowId, int topSize) {
        List<Integer> integers = packet.getIntegers().getValues();
        // 不同版本的 SET_SLOT 整数数量不同，最后一个整数始终是槽位索引。
        int slot = integers.get(integers.size() - 1);
        boolean playerInventorySlot = (windowId == 0 || windowId == -2)
                ? slot >= 9 && slot <= 44
                : windowId > 0 && slot >= topSize && slot < topSize + 36;
        if (playerInventorySlot) {
            packet.getItemModifier().write(0, new ItemStack(Material.AIR));
        }
    }

    /**
     * 隐藏整组槽位数据包中的玩家背包物品.
     *
     * @param packet 数据包
     * @param windowId 窗口 ID
     * @param topSize 顶部容器大小
     */
    private void hideWindowItems(PacketContainer packet, int windowId, int topSize) {
        List<ItemStack> items = packet.getItemListModifier().read(0);
        if (items == null) {
            return;
        }
        List<ItemStack> hiddenItems = new ArrayList<>(items);
        // windowId 为 0/-2 时是玩家背包；其他窗口从顶部容器大小之后开始计算。
        int start = windowId == 0 || windowId == -2 ? 9 : topSize;
        int end = Math.min(start + 36, hiddenItems.size());
        for (int index = start; index < end; index++) {
            hiddenItems.set(index, new ItemStack(Material.AIR));
        }
        packet.getItemListModifier().write(0, hiddenItems);
    }
}
