package cn.handyplus.menu.listener.gui;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.menu.PlayerMenu;
import cn.handyplus.menu.constants.GuiTypeEnum;
import cn.handyplus.menu.enter.MenuLimit;
import cn.handyplus.menu.hook.PlaceholderApiUtil;
import cn.handyplus.menu.hook.PlayerPointsUtil;
import cn.handyplus.menu.hook.VaultUtil;
import cn.handyplus.menu.param.MenuButtonParam;
import cn.handyplus.menu.service.MenuLimitService;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
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
            this.playSound(player, StrUtil.isNotEmpty(menuButtonParam.getFailSound()) ? menuButtonParam.getFailSound() : menuButtonParam.getSound());
            return;
        }
        // 记录点击事件
        this.setManuLimit(player, menuButtonParam);
        // 执行命令
        this.extractedCommand(player, menuButtonParam.getCommands());
        // 播放声音
        playSound(player, menuButtonParam.getSound());
    }

    /**
     * 播放声音
     *
     * @param player   玩家
     * @param soundStr 声音
     */
    private void playSound(Player player, String soundStr) {
        if (StrUtil.isEmpty(soundStr)) {
            return;
        }
        Sound sound;
        try {
            sound = Sound.valueOf(soundStr);
        } catch (Exception e) {
            MessageApi.sendMessage(player, "没有 " + soundStr + " 音效");
            return;
        }
        player.getWorld().playSound(player.getLocation(), sound, 1, 1);
    }

    /**
     * 执行命令
     *
     * @param player   玩家
     * @param commands 命令
     */
    private void extractedCommand(Player player, List<String> commands) {
        if (CollUtil.isEmpty(commands)) {
            return;
        }
        for (String command : commands) {
            command = PlaceholderApiUtil.set(player, command);
            if (command.contains("[message]")) {
                String trimMessage = command.replace("[message]", "").trim();
                MessageApi.sendMessage(player, trimMessage);
                continue;
            }
            if (command.contains("[allMessage]")) {
                String trimMessage = command.replace("[allMessage]", "").trim();
                MessageApi.sendAllMessage(trimMessage);
                continue;
            }
            if (command.contains("[title]")) {
                String trimMessage = command.replace("[title]", "").trim();
                String[] split = trimMessage.split(":");
                String title = "";
                String subTitle = "";
                title = split[0];
                if (split.length > 1) {
                    subTitle = split[1];
                }
                MessageApi.sendTitle(player, title, subTitle);
                continue;
            }
            if (command.contains("[allTitle]")) {
                String trimMessage = command.replace("[allTitle]", "").trim();
                String[] split = trimMessage.split(":");
                String title = "";
                String subTitle = "";
                title = split[0];
                if (split.length > 1) {
                    subTitle = split[1];
                }
                MessageApi.sendAllTitle(title, subTitle);
                continue;
            }
            if (command.contains("[actionbar]")) {
                String trimMessage = command.replace("[actionbar]", "").trim();
                MessageApi.sendActionbar(player, trimMessage);
                continue;
            }
            if (command.contains("[allActionbar]")) {
                String trimMessage = command.replace("[allActionbar]", "").trim();
                MessageApi.sendAllActionbar(trimMessage);
                continue;
            }
            if (command.contains("[command]")) {
                String trimCommand = command.replace("[command]", "").trim();
                player.performCommand(trimCommand);
                continue;
            }
            if (command.contains("[op]")) {
                boolean op = player.isOp();
                try {
                    String trimCommand = command.replace("[op]", "").trim();
                    player.setOp(true);
                    player.performCommand(trimCommand);
                } finally {
                    player.setOp(op);
                }
                continue;
            }
            if (command.contains("[Console]")) {
                String trimCommand = command.replace("[Console]", "").trim();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), trimCommand);
                continue;
            }
            if (command.contains("[close]")) {
                player.closeInventory();
            }
        }
    }

    /**
     * 记录点击事件
     *
     * @param player          玩家
     * @param menuButtonParam 菜单
     */
    private void setManuLimit(Player player, MenuButtonParam menuButtonParam) {
        if (menuButtonParam.getId() == null || menuButtonParam.getId() < 1 || menuButtonParam.getLimit() < 1) {
            return;
        }
        MenuLimit menuLimit = new MenuLimit();
        menuLimit.setPlayerName(player.getName());
        menuLimit.setPlayerUuid(player.getUniqueId().toString());
        menuLimit.setMenuItemId(menuButtonParam.getId());
        menuLimit.setNumber(1);
        // 异步保存数据
        Bukkit.getScheduler().runTaskAsynchronously(PlayerMenu.getInstance(), () -> MenuLimitService.getInstance().set(menuLimit));
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
            Integer count = MenuLimitService.getInstance().findCountByPlayerName(player.getName(), menuButtonParam.getId());
            if (count >= limit) {
                MessageApi.sendMessage(player, BaseUtil.getLangMsg("noLimit"));
                return true;
            }
        }
        // 判断点击金钱是否满足
        int money = menuButtonParam.getMoney();
        if (money > 0 && VaultUtil.getPlayerVault(player) < money) {
            MessageApi.sendMessage(player, BaseUtil.getLangMsg("noMoney"));
            return true;
        }
        // 判断点击点券是否满足
        int point = menuButtonParam.getPoint();
        if (point > 0 && PlayerPointsUtil.getPlayerPoints(player) < point) {
            MessageApi.sendMessage(player, BaseUtil.getLangMsg("noPoint"));
            return true;
        }
        // 判断点击自定义条件是否满足
        if (CollUtil.isNotEmpty(menuButtonParam.getConditions())) {
            for (String condition : menuButtonParam.getConditions()) {
                if (!condition.contains("=") && !condition.contains(">") && !condition.contains("<") &&
                        !condition.contains("!=") && !condition.contains(">=") && !condition.contains("<=")) {
                    MessageApi.sendMessage(player, BaseUtil.getLangMsg("errorCondition"));
                    return true;
                }
                // 判断条件
                if (!this.checkCondition(player, condition)) {
                    MessageApi.sendMessage(player, BaseUtil.getLangMsg("noOpenCondition"));
                    return true;
                }
            }
        }
        // 金币扣除处理
        if (money > 0) {
            if (!VaultUtil.buy(player, money)) {
                MessageApi.sendMessage(player, BaseUtil.getLangMsg("noMoney"));
                return true;
            }
        }
        // 点券扣除处理
        if (point > 0) {
            if (!PlayerPointsUtil.buy(player, point)) {
                MessageApi.sendMessage(player, BaseUtil.getLangMsg("noPoint"));
                return true;
            }
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
            if (number.get(0) >= number.get(1)) {
                return true;
            }
        }
        if (condition.contains("<=")) {
            List<Long> number = this.getNumber(player, condition, "<=");
            if (number.get(0) <= number.get(1)) {
                return true;
            }
        }
        if (condition.contains("!=")) {
            List<String> number = this.getStr(player, condition, "!=");
            if (!number.get(0).equals(number.get(1))) {
                return true;
            }
        }
        if (condition.contains("=")) {
            List<String> number = this.getStr(player, condition, "=");
            if (number.get(0).equals(number.get(1))) {
                return true;
            }
        }
        if (condition.contains(">")) {
            List<Long> number = this.getNumber(player, condition, ">");
            if (number.get(0) > number.get(1)) {
                return true;
            }
        }
        if (condition.contains("<")) {
            List<Long> number = this.getNumber(player, condition, "<");
            if (number.get(0) < number.get(1)) {
                return true;
            }
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
        long oneNumber = BaseUtil.isNumericToLong(one);
        long twoNumber = BaseUtil.isNumericToLong(two);
        MessageApi.sendDebugMessage(player, "条件一 " + one + " 条件二 " + two);
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
        MessageApi.sendDebugMessage(player, "条件一 " + one + " 条件二 " + two);
        return Arrays.asList(one, two);
    }

}