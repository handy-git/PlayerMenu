package cn.handyplus.menu.listener.gui;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.DateUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.enter.MenuLimit;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.hook.PlayerPointsUtil;
import cn.handyplus.menu.hook.VaultUtil;
import cn.handyplus.menu.inventory.MenuGui;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.service.MenuLimitService;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.MenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        if (this.check(player, menuButtonParam)) {
            // 播放未满足条件的声音
            MenuUtil.playSound(player, StrUtil.isNotEmpty(menuButtonParam.getFailSound()) ? menuButtonParam.getFailSound() : menuButtonParam.getSound());
            return;
        }
        // 处理商店类型
        if (this.shopCheck(player, menuButtonParam)) {
            // 播放未满足条件的声音
            MenuUtil.playSound(player, StrUtil.isNotEmpty(menuButtonParam.getFailSound()) ? menuButtonParam.getFailSound() : menuButtonParam.getSound());
            return;
        }
        // 记录点击时间
        this.setManuTimeLimit(player, menuButtonParam);
        // 记录点击次数
        this.setManuNumberLimit(player, menuButtonParam);
        // 播放声音
        MenuUtil.playSound(player, menuButtonParam.getSound());
        // 执行命令
        this.extractedCommand(player, menuButtonParam.getCommands(), handyInventory);
    }

    /**
     * 执行命令
     *
     * @param player         玩家
     * @param commands       命令
     * @param handyInventory gui
     */
    private void extractedCommand(Player player, List<String> commands, HandyInventory handyInventory) {
        if (CollUtil.isEmpty(commands)) {
            return;
        }
        for (String command : commands) {
            command = PlaceholderApiUtil.set(player, command);
            if (command.contains("[message]")) {
                String trimMessage = command.replace("[message]", "").trim();
                MessageUtil.sendMessage(player, trimMessage);
                continue;
            }
            if (command.contains("[allMessage]")) {
                String trimMessage = command.replace("[allMessage]", "").trim();
                MessageUtil.sendAllMessage(trimMessage);
                continue;
            }
            if (command.contains("[title]")) {
                String trimMessage = command.replace("[title]", "").trim();
                String[] split = trimMessage.split(":");
                String title;
                String subTitle = "";
                title = split[0];
                if (split.length > 1) {
                    subTitle = split[1];
                }
                MessageUtil.sendTitle(player, title, subTitle);
                continue;
            }
            if (command.contains("[allTitle]")) {
                String trimMessage = command.replace("[allTitle]", "").trim();
                String[] split = trimMessage.split(":");
                String title;
                String subTitle = "";
                title = split[0];
                if (split.length > 1) {
                    subTitle = split[1];
                }
                MessageUtil.sendAllTitle(title, subTitle);
                continue;
            }
            if (command.contains("[actionbar]")) {
                String trimMessage = command.replace("[actionbar]", "").trim();
                MessageUtil.sendActionbar(player, trimMessage);
                continue;
            }
            if (command.contains("[allActionbar]")) {
                String trimMessage = command.replace("[allActionbar]", "").trim();
                MessageUtil.sendAllActionbar(trimMessage);
                continue;
            }
            if (command.contains("[command]")) {
                PlayerSchedulerUtil.performCommand(player, command.replace("[command]", ""));
                continue;
            }
            if (command.contains("[op]")) {
                PlayerSchedulerUtil.performOpCommand(player, command.replace("[op]", ""));
                continue;
            }
            if (command.contains("[Console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[Console]", "").trim());
                continue;
            }
            if (command.contains("[console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[console]", "").trim());
                continue;
            }
            if (command.contains("[close]")) {
                player.closeInventory();
                continue;
            }
            if (command.contains("[server]")) {
                String trimCommand = command.replace("[server]", "").trim();
                BcUtil.tpConnect(player, trimCommand);
                continue;
            }
            if (command.contains("[open]")) {
                String menu = command.replace("[open]", "").trim();
                MenuUtil.openGui(player, menu);
                continue;
            }
            // 1.2.5 添加刷新gui节点
            if (command.contains("[refresh]")) {
                MenuGui.getInstance().setInventoryDate(handyInventory);
            }
        }
    }

    /**
     * 记录点击时间
     *
     * @param player          玩家
     * @param menuButtonParam 菜单
     */
    private void setManuTimeLimit(Player player, MenuButtonParam menuButtonParam) {
        if (menuButtonParam.getId() == null || menuButtonParam.getCd() < 1) {
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
    private void setManuNumberLimit(Player player, MenuButtonParam menuButtonParam) {
        if (menuButtonParam.getId() == null || menuButtonParam.getLimit() < 1) {
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
    private boolean check(Player player, MenuButtonParam menuButtonParam) {
        // 判断点击次数处理
        int limit = menuButtonParam.getLimit();
        if (limit > 0) {
            Integer count = MenuLimitService.getInstance().findCountByPlayerUuid(player.getUniqueId(), menuButtonParam.getId());
            if (count >= limit) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noLimit"));
                return true;
            }
        }
        // 判断点击时间
        int cd = menuButtonParam.getCd();
        if (cd > 0) {
            Date clickTime = MenuLimitService.getInstance().findTimeByPlayerUuid(player.getUniqueId(), menuButtonParam.getId());
            if (clickTime != null) {
                long time = DateUtil.offset(clickTime, Calendar.SECOND, cd).getTime() - System.currentTimeMillis();
                if (time > 0) {
                    String noTimeLimit = ConfigUtil.LANG_CONFIG.getString("noTimeLimit", "");
                    MessageUtil.sendMessage(player, StrUtil.replace(noTimeLimit, "time", String.valueOf((int) time / 1000)));
                    return true;
                }
            }
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
                if (!this.checkCondition(player, condition)) {
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
    public boolean shopCheck(Player player, MenuButtonParam menuButtonParam) {
        String shopType = menuButtonParam.getShopType();
        String shopMaterial = menuButtonParam.getShopMaterial();
        if (StrUtil.isEmpty(shopType) || StrUtil.isEmpty(shopMaterial)) {
            return false;
        }
        // 玩家购买物品
        if ("buy".equalsIgnoreCase(shopType)) {
            // 判断点击金钱是否满足
            int shopMoney = menuButtonParam.getShopMoney();
            if (shopMoney > 0 && VaultUtil.getPlayerVault(player) < shopMoney) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMoney"));
                return true;
            }
            // 判断点击点券是否满足
            int shopPoint = menuButtonParam.getShopPoint();
            if (shopPoint > 0 && PlayerPointsUtil.getPlayerPoints(player) < shopPoint) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPoint"));
                return true;
            }
            // 金币扣除处理
            if (shopMoney > 0) {
                if (!VaultUtil.buy(player, shopMoney)) {
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noMoney"));
                    return true;
                }
            }
            // 点券扣除处理
            if (shopPoint > 0) {
                if (!PlayerPointsUtil.buy(player, shopPoint)) {
                    MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPoint"));
                    return true;
                }
            }
            // 发送物品
            String[] shopMaterialStr = shopMaterial.split(":");
            String material = shopMaterialStr[0];
            String number = shopMaterialStr[1];
            ItemStack itemStack = new ItemStack(ItemStackUtil.getMaterial(material));
            ItemStackUtil.addItem(player.getInventory(), itemStack, Integer.parseInt(number));
            MessageUtil.sendMessage(player, ConfigUtil.LANG_CONFIG.getString("buyMsg"));
            return false;
        }
        // 玩家出售物品
        if ("sell".equalsIgnoreCase(shopType)) {
            String[] shopMaterialStr = shopMaterial.split(":");
            String material = shopMaterialStr[0];
            String number = shopMaterialStr[1];
            Boolean rst = ItemStackUtil.removeItem(player.getInventory(), new ItemStack(ItemStackUtil.getMaterial(material)), Integer.valueOf(number));
            if (!rst) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noItem"));
                return true;
            }
            int shopMoney = menuButtonParam.getShopMoney();
            int shopPoint = menuButtonParam.getShopPoint();
            VaultUtil.give(player, shopMoney);
            PlayerPointsUtil.give(player, shopPoint);
            MessageUtil.sendMessage(player, ConfigUtil.LANG_CONFIG.getString("sellMsg"));
        }
        return false;
    }

    /**
     * 判断点击自定义条件是否满足
     *
     * @param player    玩家
     * @param condition 条件
     * @return true 满足
     */
    private boolean checkCondition(Player player, String condition) {
        if (condition.contains(">=")) {
            List<Long> number = this.getNumber(player, condition, ">=");
            return number.get(0) >= number.get(1);
        }
        if (condition.contains("<=")) {
            List<Long> number = this.getNumber(player, condition, "<=");
            return number.get(0) <= number.get(1);
        }
        if (condition.contains("!=")) {
            List<String> number = this.getStr(player, condition, "!=");
            return !number.get(0).equals(number.get(1));
        }
        if (condition.contains("=")) {
            List<String> number = this.getStr(player, condition, "=");
            return number.get(0).equals(number.get(1));
        }
        if (condition.contains(">")) {
            List<Long> number = this.getNumber(player, condition, ">");
            return number.get(0) > number.get(1);
        }
        if (condition.contains("<")) {
            List<Long> number = this.getNumber(player, condition, "<");
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
    private List<Long> getNumber(Player player, String condition, String splitStr) {
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
    private List<String> getStr(Player player, String condition, String splitStr) {
        String[] split = condition.split(splitStr);
        String one = split[0].trim();
        String two = split[1].trim();
        if (PlayerMenu.USE_PAPI) {
            one = PlaceholderApiUtil.set(player, one);
            two = PlaceholderApiUtil.set(player, two);
        }
        MessageUtil.sendDebugMessage(player, "条件一 " + one + " 条件二 " + two);
        return Arrays.asList(one, two);
    }

}