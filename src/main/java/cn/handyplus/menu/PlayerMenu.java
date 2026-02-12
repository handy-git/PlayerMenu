package cn.handyplus.menu;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.HookPluginEnum;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HookPluginUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.hook.PlaceholderUtil;
import cn.handyplus.menu.util.ConfigUtil;
import cn.handyplus.menu.util.UpUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * 主类
 *
 * @author handy
 */
public class PlayerMenu extends JavaPlugin {
    public static PlayerMenu INSTANCE;
    public static boolean USE_PAPI;
    public static Economy ECON;
    public static PlayerPoints PLAYER_POINTS;
    public static boolean USE_GUILD;
    public static boolean USE_PLY;

    @Override
    public void onEnable() {
        INSTANCE = this;
        InitApi initApi = InitApi.getInstance(this);
        // 加载 配置文件
        ConfigUtil.init();
        // 加载 vault
        this.loadEconomy();
        // 加载 PlayerPoints
        HookPluginUtil.hookToPlugin(HookPluginEnum.PLAYER_POINTS).ifPresent(value -> PLAYER_POINTS = (PlayerPoints) value);
        // 加载 PlaceholderApi
        USE_PAPI = HookPluginUtil.hook(HookPluginEnum.PLACEHOLDER_API);
        if (USE_PAPI) {
            new PlaceholderUtil(this).register();
        }
        // 加载 PlayerGuild
        USE_GUILD = HookPluginUtil.hook(HookPluginEnum.PLAYER_GUILD);
        // 加载 PlayerCurrency
        USE_PLY = HookPluginUtil.hook(HookPluginEnum.PLAYER_CURRENCY);
        List<String> lordList = Arrays.asList(
                "",
                "  ____  _                       __  __                  ",
                " |  _  | | __ _ _   _  ___ _ __|  /  | ___ _ __  _   _ ",
                " | |_) | |/ _` | | | |/ _ \\ '__| |/| |/ _ \\ '_ \\| | | |",
                " |  __/| | (_| | |_| |  __/ |  | |  | |  __/ | | | |_| |",
                " |_|   |_|\\__,_|\\__, |\\___|_|  |_|  |_|\\___|_| |_|\\__,_|",
                "                |___/                                   "
        );
        for (String lord : lordList) {
            MessageUtil.sendConsoleMessage(ChatColor.DARK_AQUA + lord);
        }
        initApi.addMetrics(14034)
                .initCommand("cn.handyplus.menu.command")
                .initListener("cn.handyplus.menu.listener")
                .initClickEvent("cn.handyplus.menu.listener.gui")
                .enableSql("cn.handyplus.menu.enter")
                .enableBc()
                .checkVersion();

        UpUtil.up();

        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "已成功载入服务器！");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "Author:handy WIKI: https://ricedoc.handyplus.cn/wiki/PlayerMenu/README/");
    }

    @Override
    public void onDisable() {
        InitApi.disable();
    }

    /**
     * 加载 Vault
     */
    private void loadEconomy() {
        if (getServer().getPluginManager().getPlugin(HookPluginEnum.VAULT.getName()) == null) {
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg(HookPluginEnum.VAULT.getFailMsg()));
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg(HookPluginEnum.VAULT.getFailMsg()));
            return;
        }
        ECON = rsp.getProvider();
        MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg(HookPluginEnum.VAULT.getSuccessMsg()));
    }

}