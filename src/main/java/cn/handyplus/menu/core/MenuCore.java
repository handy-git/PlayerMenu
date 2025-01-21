package cn.handyplus.menu.core;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.FormulaUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.CommandTypeEnum;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.enter.MenuLimit;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.hook.PlayerCurrencyUtil;
import cn.handyplus.menu.hook.PlayerPointsUtil;
import cn.handyplus.menu.hook.VaultUtil;
import cn.handyplus.menu.inventory.MenuGui;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.service.MenuLimitService;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单逻辑核心
 *
 * @author handy
 * @since 1.3.7
 */
public class MenuCore {

    /**
     * 执行
     *
     * @param player          玩家
     * @param menuButtonParam 菜单属性
     */
    public static void executeMenu(Player player, MenuButtonParam menuButtonParam) {
        executeMenu(null, player, menuButtonParam);
    }

    /**
     * 执行
     *
     * @param handyInventory  gui
     * @param player          玩家
     * @param menuButtonParam 菜单属性
     */
    public static void executeMenu(HandyInventory handyInventory, Player player, MenuButtonParam menuButtonParam) {
        // 处理商店类型
        if (shopCheck(player, menuButtonParam)) {
            // 播放未满足条件的声音
            MenuUtil.playSound(player, StrUtil.isNotEmpty(menuButtonParam.getFailSound()) ? menuButtonParam.getFailSound() : menuButtonParam.getSound());
            return;
        }
        // 记录点击时间
        setManuTimeLimit(player, menuButtonParam);
        // 记录点击次数
        setManuNumberLimit(player, menuButtonParam);
        // 播放声音
        MenuUtil.playSound(player, menuButtonParam.getSound());
        // 执行命令
        executeCommand(player, menuButtonParam.getCommands(), handyInventory, 0);
        // 执行右键命令
        if (ClickType.RIGHT.equals(menuButtonParam.getEventClickType())) {
            executeCommand(player, menuButtonParam.getActions().get("right"), handyInventory, 0);
        }
        // 执行左键命令
        if (ClickType.LEFT.equals(menuButtonParam.getEventClickType())) {
            executeCommand(player, menuButtonParam.getActions().get("left"), handyInventory, 0);
        }
    }

    /**
     * 执行命令
     *
     * @param player         玩家
     * @param commands       命令
     * @param handyInventory gui
     * @param index          递归基数
     */
    private static void executeCommand(Player player, List<String> commands, HandyInventory handyInventory, Integer index) {
        if (CollUtil.isEmpty(commands)) {
            return;
        }
        // 结束递归
        if (index >= commands.size()) {
            return;
        }
        String command = commands.get(index);
        if (StrUtil.isEmpty(command)) {
            MessageUtil.sendConsoleMessage("配置异常:节点commands出现空行");
            return;
        }
        command = PlaceholderApiUtil.set(player, command);
        command = replaceInput(player, command);
        CommandTypeEnum commandTypeEnum = CommandTypeEnum.contains(command);
        if (commandTypeEnum == null) {
            MessageUtil.sendConsoleMessage("配置异常:节点commands出现不支持的类型:" + command);
            return;
        }
        String content = CommandTypeEnum.replaceFirst(command, commandTypeEnum);
        switch (commandTypeEnum) {
            case DELAY:
                Integer delay = NumberUtil.isNumericToInt(content, 0);
                HandySchedulerUtil.runTaskLater(() -> executeCommand(player, commands, handyInventory, index + 1), delay * 20);
                return;
            case MESSAGE:
                MessageUtil.sendMessage(player, content);
                break;
            case ALL_MESSAGE:
                MessageUtil.sendAllMessage(content);
                break;
            case TITLE:
                String[] split = content.split(":");
                MessageUtil.sendTitle(player, split[0], split.length > 1 ? split[1] : "");
                break;
            case ALL_TITLE:
                String[] allSplit = content.split(":");
                MessageUtil.sendAllTitle(allSplit[0], allSplit.length > 1 ? allSplit[1] : "");
                break;
            case ACTIONBAR:
                MessageUtil.sendActionbar(player, content);
                break;
            case ALL_ACTIONBAR:
                MessageUtil.sendAllActionbar(content);
                break;
            case COMMAND:
                PlayerSchedulerUtil.performCommand(player, content);
                break;
            case OP:
                PlayerSchedulerUtil.performOpCommand(player, content);
                break;
            case CONSOLE:
                PlayerSchedulerUtil.dispatchCommand(content);
                break;
            case CLOSE:
                player.closeInventory();
                break;
            case SERVER:
                BcUtil.tpConnect(player, content);
                break;
            case OPEN:
                MenuUtil.openGui(player, content);
                break;
            case REFRESH:
                if (handyInventory != null) {
                    MenuGui.getInstance().setInventoryDate(handyInventory);
                }
                break;
            case PERFORM_COMMAND:
                PlayerSchedulerUtil.playerPerformCommand(player, content);
                break;
            case OP_PERFORM_COMMAND:
                PlayerSchedulerUtil.playerPerformOpCommand(player, content);
                break;
            default:
                break;
        }
        // 递归执行下一个命令
        executeCommand(player, commands, handyInventory, index + 1);
    }

    /**
     * 记录点击时间
     *
     * @param player          玩家
     * @param menuButtonParam 菜单
     */
    private static void setManuTimeLimit(Player player, MenuButtonParam menuButtonParam) {
        if (menuButtonParam.getId() == null) {
            return;
        }
        if (menuButtonParam.getCd() < 1 && menuButtonParam.getCdHide() < 1) {
            return;
        }
        MenuLimit menuLimit = new MenuLimit();
        menuLimit.setPlayerName(player.getName());
        menuLimit.setPlayerUuid(player.getUniqueId().toString());
        menuLimit.setMenuItemId(menuButtonParam.getId());
        menuLimit.setNumber(0);
        menuLimit.setClickTime(new Date());
        // 保存数据
        MenuLimitService.getInstance().setClickTimeById(menuLimit);
    }

    /**
     * 记录点击次数
     *
     * @param player          玩家
     * @param menuButtonParam 菜单
     */
    private static void setManuNumberLimit(Player player, MenuButtonParam menuButtonParam) {
        if (menuButtonParam.getId() == null) {
            return;
        }
        if (menuButtonParam.getLimit() < 1 && menuButtonParam.getLimitHide() < 1) {
            return;
        }
        MenuLimit menuLimit = new MenuLimit();
        menuLimit.setPlayerName(player.getName());
        menuLimit.setPlayerUuid(player.getUniqueId().toString());
        menuLimit.setMenuItemId(menuButtonParam.getId());
        menuLimit.setNumber(1);
        menuLimit.setClickTime(new Date());
        // 异步保存数据
        MenuLimitService.getInstance().addNumberById(menuLimit);
    }

    /**
     * 检查点击条件是否满足
     *
     * @param player          玩家
     * @param menuButtonParam 菜单
     * @return true 不满足
     */
    public static boolean check(Player player, MenuButtonParam menuButtonParam) {
        // 判断点击次数处理
        if (MenuUtil.clickLimit(player, menuButtonParam.getId(), menuButtonParam.getLimit(), true)) {
            return true;
        }
        // 判断点击次数处理
        if (MenuUtil.clickLimit(player, menuButtonParam.getId(), menuButtonParam.getLimitHide(), true)) {
            return true;
        }
        // 判断点击时间
        if (MenuUtil.clickCd(player, menuButtonParam.getId(), menuButtonParam.getCd(), true)) {
            return true;
        }
        // 判断点击类型是否满足
        if (StrUtil.isNotEmpty(menuButtonParam.getClickType()) && !menuButtonParam.getEventClickType().name().equalsIgnoreCase(menuButtonParam.getClickType())) {
            return true;
        }
        // 判断点击金钱是否满足
        int money = menuButtonParam.getMoney();
        if (money > 0 && VaultUtil.getPlayerVault(player) < money) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMoney"));
            return true;
        }
        // 判断点击点券是否满足
        int point = menuButtonParam.getPoint();
        if (point > 0 && PlayerPointsUtil.getPlayerPoints(player) < point) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPoint"));
            return true;
        }
        // 判断点击自定义条件是否满足
        if (CollUtil.isNotEmpty(menuButtonParam.getConditions())) {
            for (String condition : menuButtonParam.getConditions()) {
                if (!condition.contains("=") && !condition.contains(">") && !condition.contains("<") &&
                        !condition.contains("!=") && !condition.contains(">=") && !condition.contains("<=")) {
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("errorCondition"));
                    return true;
                }
                // 判断条件
                if (!checkCondition(player, condition)) {
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noOpenCondition"));
                    return true;
                }
            }
        }
        // 金币扣除处理
        if (money > 0) {
            if (!VaultUtil.buy(player, money)) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMoney"));
                return true;
            }
        }
        // 点券扣除处理
        if (point > 0) {
            if (!PlayerPointsUtil.buy(player, point)) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPoint"));
                return true;
            }
        }
        // 商店判断
        return false;
    }

    /**
     * 商店判断
     *
     * @param player          玩家
     * @param menuButtonParam 菜单
     * @return true 不满足
     * @since 1.2.0
     */
    private static boolean shopCheck(Player player, MenuButtonParam menuButtonParam) {
        String shopType = menuButtonParam.getShopType();
        String shopMaterial = menuButtonParam.getShopMaterial();
        if (StrUtil.isEmpty(shopType) || StrUtil.isEmpty(shopMaterial)) {
            return false;
        }
        // 金币处理
        String input = MenuConstants.PLAYER_INPUT_MAP.getOrDefault(player.getUniqueId(), "");
        int shopMoney = getShopPrice(menuButtonParam.getShopMoney(), input);
        // 点券处理
        int shopPoint = getShopPrice(menuButtonParam.getShopPoint(), input);
        // 多货币处理
        int currencyPrice = 0;
        String currencyType = null;
        if (StrUtil.isNotEmpty(menuButtonParam.getShopCurrency())) {
            List<String> shopCurrencyList = StrUtil.strToStrList(menuButtonParam.getShopCurrency(), ":");
            currencyType = shopCurrencyList.get(0).trim();
            currencyPrice = getShopPrice(shopCurrencyList.get(1).trim(), input);
        }
        Map<String, String> replaceMap = MapUtil.newHashMapWithExpectedSize(4);
        // 玩家购买物品
        if (MenuConstants.BUY.equalsIgnoreCase(shopType)) {
            // 金钱是否满足
            if (shopMoney > 0 && VaultUtil.getPlayerVault(player) < shopMoney) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMoney"));
                return true;
            }
            // 点券是否满足
            if (shopPoint > 0 && PlayerPointsUtil.getPlayerPoints(player) < shopPoint) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPoint"));
                return true;
            }
            // 金币扣除处理
            if (shopMoney > 0) {
                replaceMap.put("${price}", String.valueOf(shopMoney));
                replaceMap.put("${type}", BaseUtil.getMsgNotColor("money"));
                if (!VaultUtil.buy(player, shopMoney)) {
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMoney"));
                    return true;
                }
            }
            // 点券扣除处理
            if (shopPoint > 0) {
                replaceMap.put("${price}", String.valueOf(shopPoint));
                replaceMap.put("${type}", BaseUtil.getMsgNotColor("point"));
                if (!PlayerPointsUtil.buy(player, shopPoint)) {
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPoint"));
                    return true;
                }
            }
            // 先获取购买的物品
            String[] shopMaterialStr = shopMaterial.split(":");
            String material = shopMaterialStr[0];
            String number = replaceInput(player, shopMaterialStr[1]);
            ItemStack itemStack = new ItemStack(ItemStackUtil.getMaterial(material));
            // 多经济处理
            if (currencyPrice > 0) {
                replaceMap.put("${price}", String.valueOf(currencyPrice));
                replaceMap.put("${type}", PlayerCurrencyUtil.getDesc(currencyType));
                String buyOperatorReason = BaseUtil.getMsgNotColor("buyOperatorReason", MapUtil.of("${name}", BaseUtil.getDisplayName(itemStack), "${number}", number));
                if (!PlayerCurrencyUtil.buy(player, currencyType, currencyPrice, buyOperatorReason)) {
                    HashMap<String, String> map = MapUtil.of("${type}", PlayerCurrencyUtil.getDesc(currencyType));
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noBalance", map));
                    return true;
                }
            }
            // 发送物品
            replaceMap.put("${number}", String.valueOf(number));
            replaceMap.put("${name}", BaseUtil.getDisplayName(itemStack));
            ItemStackUtil.addItem(player, itemStack, Integer.parseInt(number), BaseUtil.getMsgNotColor("addItemMsg"));
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("buyMsg", replaceMap));
            return false;
        }
        // 玩家出售物品
        if (MenuConstants.SELL.equalsIgnoreCase(shopType)) {
            String[] shopMaterialStr = shopMaterial.split(":");
            String material = shopMaterialStr[0];
            String number = replaceInput(player, shopMaterialStr[1]);
            ItemStack itemStack = new ItemStack(ItemStackUtil.getMaterial(material));
            Boolean rst = ItemStackUtil.removeItem(player, itemStack, Integer.valueOf(number), false);
            if (!rst) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noItem"));
                return true;
            }
            if (shopMoney > 0) {
                replaceMap.put("${price}", String.valueOf(shopMoney));
                replaceMap.put("${type}", BaseUtil.getMsgNotColor("money"));
                VaultUtil.give(player, shopMoney);
            }
            if (shopPoint > 0) {
                replaceMap.put("${price}", String.valueOf(shopPoint));
                replaceMap.put("${type}", BaseUtil.getMsgNotColor("point"));
                PlayerPointsUtil.give(player, shopPoint);
            }
            if (currencyPrice > 0) {
                replaceMap.put("${price}", String.valueOf(currencyPrice));
                replaceMap.put("${type}", PlayerCurrencyUtil.getDesc(currencyType));
                String sellOperatorReason = BaseUtil.getMsgNotColor("sellOperatorReason", MapUtil.of("${name}", BaseUtil.getDisplayName(itemStack), "${number}", number));
                PlayerCurrencyUtil.give(player, currencyType, currencyPrice, sellOperatorReason);
            }
            replaceMap.put("${number}", number);
            replaceMap.put("${name}", BaseUtil.getDisplayName(itemStack));
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("sellMsg", replaceMap));
        }
        return false;
    }

    /**
     * 获取商品价格
     *
     * @param shopPriceStr 商品价格
     * @param input        输入值
     * @return 价格
     */
    private static int getShopPrice(String shopPriceStr, String input) {
        int shopPrice = 0;
        if (StrUtil.isEmpty(shopPriceStr)) {
            return shopPrice;
        }
        // 价格格式处理
        if (NumberUtil.isNumericToInt(shopPriceStr) != null) {
            shopPrice = NumberUtil.isNumericToInt(shopPriceStr);
        } else {
            shopPrice = FormulaUtil.evaluateFormulaToInt(shopPriceStr, MapUtil.of("input", input));
        }
        return shopPrice;
    }

    /**
     * 判断点击自定义条件是否满足
     *
     * @param player    玩家
     * @param condition 条件
     * @return true 满足
     */
    private static boolean checkCondition(Player player, String condition) {
        if (condition.contains(">=")) {
            List<Long> number = getNumber(player, condition, ">=");
            return number.get(0) >= number.get(1);
        }
        if (condition.contains("<=")) {
            List<Long> number = getNumber(player, condition, "<=");
            return number.get(0) <= number.get(1);
        }
        if (condition.contains("!=")) {
            List<String> number = getStr(player, condition, "!=");
            return !number.get(0).equals(number.get(1));
        }
        if (condition.contains("=")) {
            List<String> number = getStr(player, condition, "=");
            return number.get(0).equals(number.get(1));
        }
        if (condition.contains(">")) {
            List<Long> number = getNumber(player, condition, ">");
            return number.get(0) > number.get(1);
        }
        if (condition.contains("<")) {
            List<Long> number = getNumber(player, condition, "<");
            return number.get(0) < number.get(1);
        }
        return false;
    }

    /**
     * 获取条件值
     *
     * @param player    玩家
     * @param condition 条件
     * @param splitStr  分割符号
     * @return 值
     */
    private static List<Long> getNumber(Player player, String condition, String splitStr) {
        String[] split = condition.split(splitStr);
        String one = split[0].trim();
        String two = split[1].trim();
        if (PlayerMenu.USE_PAPI) {
            one = PlaceholderApiUtil.set(player, one);
            two = PlaceholderApiUtil.set(player, two);
        }
        Long oneNumber = NumberUtil.isNumericToLong(one);
        Long twoNumber = NumberUtil.isNumericToLong(two);
        MessageUtil.sendDebugMessage(player, "条件一 " + one + " 条件二 " + two);
        return Arrays.asList(oneNumber, twoNumber);
    }

    /**
     * 获取条件值
     *
     * @param player    玩家
     * @param condition 条件
     * @param splitStr  分割符号
     * @return 值
     */
    private static List<String> getStr(Player player, String condition, String splitStr) {
        String[] split = condition.split(splitStr);
        String one = split[0].trim();
        String two = split[1].trim();
        if (PlayerMenu.USE_PAPI) {
            one = BaseUtil.stripColor(PlaceholderApiUtil.set(player, one));
            two = BaseUtil.stripColor(PlaceholderApiUtil.set(player, two));
        }
        MessageUtil.sendDebugMessage(player, "条件一 " + one + " 条件二 " + two);
        return Arrays.asList(one, two);
    }

    /**
     * 替换input值
     *
     * @param player 玩家
     * @param str    值
     * @return input值
     * @since 1.3.7
     */
    private static String replaceInput(Player player, String str) {
        return StrUtil.replace(str, "input", MenuConstants.PLAYER_INPUT_MAP.getOrDefault(player.getUniqueId(), ""));
    }

}
