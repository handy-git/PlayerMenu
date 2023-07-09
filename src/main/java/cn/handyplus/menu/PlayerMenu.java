package cn.handyplus.menu;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.db.SqlManagerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.menu.constants.MenuConstants;
import cn.handyplus.menu.util.ConfigUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
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
    private static PlayerMenu INSTANCE;
    public static boolean USE_PAPI;
    public static Economy ECON;
    public static PlayerPoints PLAYER_POINTS;
    public static boolean USE_GUILD;

    @Override
    public void onEnable() {
        INSTANCE = this;
        InitApi initApi = InitApi.getInstance(this);
        // 加载配置文件
        ConfigUtil.init();
        // 加载vault
        this.loadEconomy();
        // 加载PlayerPoints
        this.loadPlayerPoints();
        // 加载PlaceholderApi
        USE_PAPI = BaseUtil.hook(BaseConstants.PLACEHOLDER_API, "placeholderAPISucceedMsg", "placeholderAPIFailureMsg");
        // 加载PlayerGuild
        USE_GUILD = BaseUtil.hook("PlayerGuild", "playerGuildSucceedMsg", "playerGuildFailureMsg");
        // 打印logo
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
        initApi.initCommand("cn.handyplus.menu.command")
                .initListener("cn.handyplus.menu.listener")
                .initClickEvent("cn.handyplus.menu.listener.gui")
                .enableSql("cn.handyplus.menu.enter")
                .addMetrics(14034)
                .checkVersion(ConfigUtil.CONFIG.getBoolean(BaseConstants.IS_CHECK_UPDATE), MenuConstants.PLUGIN_VERSION_URL);
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "已成功载入服务器！");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "Author:handy QQ群:1064982471");
    }

    @Override
    public void onDisable() {
        // 关闭gui
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            InventoryView openInventory = onlinePlayer.getOpenInventory();
            InventoryHolder holder = openInventory.getTopInventory().getHolder();
            if (holder instanceof HandyInventory) {
                onlinePlayer.closeInventory();
            }
        }
        // 关闭数据源
        SqlManagerUtil.getInstance().close();
        MessageUtil.sendConsoleMessage("§a已成功卸载！");
        MessageUtil.sendConsoleMessage("§aAuthor:handy QQ群:1064982471");
    }

    public static PlayerMenu getInstance() {
        return INSTANCE;
    }

    public static Economy getEconomy() {
        return ECON;
    }

    public static PlayerPoints getPlayerPoints() {
        return PLAYER_POINTS;
    }

    /**
     * 加载Vault
     */
    public void loadEconomy() {
        if (getServer().getPluginManager().getPlugin(BaseConstants.VAULT) == null) {
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        ECON = rsp.getProvider();
        MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("vaultSucceedMsg"));
    }

    /**
     * 加载PlayerPoints
     */
    private void loadPlayerPoints() {
        if (Bukkit.getPluginManager().getPlugin(BaseConstants.PLAYER_POINTS) != null) {
            final Plugin plugin = this.getServer().getPluginManager().getPlugin(BaseConstants.PLAYER_POINTS);
            PLAYER_POINTS = (PlayerPoints) plugin;
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("playerPointsSucceedMsg"));
            return;
        }
        MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("playerPointsFailureMsg"));
    }

}