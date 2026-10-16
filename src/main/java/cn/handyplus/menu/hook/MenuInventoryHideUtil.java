package cn.handyplus.menu.hook;

import cn.handyplus.lib.internal.HandySchedulerUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家菜单隐藏背包状态.
 *
 * @author handy
 */
public final class MenuInventoryHideUtil {
    private static final Map<UUID, Integer> HIDDEN_MENU_MAP = new ConcurrentHashMap<>();

    private MenuInventoryHideUtil() {
    }

    /**
     * 记录菜单顶部容器大小.
     *
     * @param player 玩家
     * @param topSize 顶部容器大小
     */
    public static void hide(Player player, int topSize) {
        HIDDEN_MENU_MAP.put(player.getUniqueId(), topSize);
    }

    /**
     * 清理隐藏状态并恢复客户端背包.
     *
     * @param player 玩家
     */
    public static void show(Player player) {
        if (HIDDEN_MENU_MAP.remove(player.getUniqueId()) == null) {
            return;
        }
        // 延迟一 tick，等待关闭窗口的客户端状态完成后再同步真实背包。
        HandySchedulerUtil.runTaskLater(() -> {
            if (player.isOnline()) {
                player.updateInventory();
            }
        }, 1L);
    }

    /**
     * 获取玩家当前菜单顶部大小.
     *
     * @param playerUuid 玩家 UUID
     * @return 顶部大小, 没有隐藏菜单时返回 null
     */
    public static Integer getTopSize(UUID playerUuid) {
        return HIDDEN_MENU_MAP.get(playerUuid);
    }

    /**
     * 清空隐藏状态.
     */
    public static void clear() {
        HIDDEN_MENU_MAP.clear();
    }
}
