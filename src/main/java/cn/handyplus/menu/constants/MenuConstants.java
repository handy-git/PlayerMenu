package cn.handyplus.menu.constants;

import cn.handyplus.menu.param.MenuButtonParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 常量
 *
 * @author handy
 */
public class MenuConstants {

    /**
     * gui固定大小
     */
    public final static List<Integer> GUI_SIZE = Arrays.asList(9, 18, 27, 36, 45, 54);

    /**
     * 属性前缀
     */
    public final static String PREFIX = "player_menu";

    /**
     * 输入类型
     *
     * @since 1.3.7
     */
    public static Map<UUID, MenuButtonParam> INPUT_MENU_MAP = new HashMap<>();

    /**
     * 玩家输入值
     *
     * @since 1.3.7
     */
    public static Map<UUID, String> PLAYER_INPUT_MAP = new HashMap<>();

    /**
     * 玩家出售
     *
     * @since 1.4.5
     */
    public static final String SELL = "sell";

    /**
     * 玩家购买
     *
     * @since 1.4.5
     */
    public static final String BUY = "buy";

}
