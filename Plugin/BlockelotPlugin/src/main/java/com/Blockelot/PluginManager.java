package com.Blockelot;

import com.Blockelot.Util.ServerUtil;
import com.Blockelot.Util.Verify;
import com.Blockelot.worldeditor.commands.About;
import com.Blockelot.worldeditor.commands.AutoPickupToggle;
import com.Blockelot.worldeditor.commands.BlockBankDeposit;
import com.Blockelot.worldeditor.commands.BlockBankInventory;
import com.Blockelot.worldeditor.commands.BlockBankWithdrawl;
import com.Blockelot.worldeditor.commands.Clear;
import com.Blockelot.worldeditor.commands.ClearHistory;
import com.Blockelot.worldeditor.commands.ClipDimensions;
import com.Blockelot.worldeditor.commands.Copy;
import com.Blockelot.worldeditor.commands.Cut;
import com.Blockelot.worldeditor.commands.Delete;
import com.Blockelot.worldeditor.commands.Demographics;
import com.Blockelot.worldeditor.commands.Help;
import com.Blockelot.worldeditor.commands.Paste;
import com.Blockelot.worldeditor.commands.Print;
import com.Blockelot.worldeditor.commands.Select;
import com.Blockelot.worldeditor.commands.StripMine;
import com.Blockelot.worldeditor.commands.Undo;
import com.Blockelot.worldeditor.commands.XpFly;
import com.Blockelot.worldeditor.commands.filesystem.Authenticate;
import com.Blockelot.worldeditor.commands.filesystem.CD;
import com.Blockelot.worldeditor.commands.filesystem.LS;
import com.Blockelot.worldeditor.commands.filesystem.LoadClipboard;
import com.Blockelot.worldeditor.commands.filesystem.MK;
import com.Blockelot.worldeditor.commands.filesystem.RM;
import com.Blockelot.worldeditor.commands.filesystem.Register;
import com.Blockelot.worldeditor.commands.filesystem.SaveClipboard;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.UUID;


import com.griefdefender.api.GriefDefender;

public class PluginManager {

    public static Tools Plugin;
    public static String Version;
    
    /**
     * A collection of PlayerInfo for each player who is logged into the server
     */
    public static HashMap<UUID, PlayerInfo> PlayerInfoList;

    /**
     * Retrieves Player  Info for the passed player UUID
     * @param key
     * @return PlayerInfo object
     */
    public static PlayerInfo GetPlayerInfo(UUID key) {
        return PlayerInfoList.get(key);
    }

    /**
     * Adds a new PlayerInfo to the active player collection.
     * @param pi
     */
    public static void AddPlayerInfo(PlayerInfo pi) {
        PlayerInfoList.put(pi.getPlayer().getUniqueId(), pi);
    }

    /**
     * Checks to see if the PlayerInfoList contains the player.
     * @param player
     * @return true or false
     */
    public static boolean HasPlayer(Player player) {
        return PlayerInfoList.containsKey(player.getUniqueId());
    }

    /**
     * Removes a PlayerInfo from the PlayerInfoList collection
     * @param player
     */
    public static void RemovePlayer(Player player) {
        if (PlayerInfoList.containsKey(player.getUniqueId())) {
            PlayerInfoList.remove(player.getUniqueId());
        }
    }
    
    /**
     * A flag that indicates if the GriefDefender plugin is loaded.
     */
    public static boolean GriefDefenderLoaded = false;

    
    private static final Logger log = Logger.getLogger("Minecraft");

    static {
        PlayerInfoList = new HashMap<>();
        Version = "1.0.1.0";
        //Config = new Configuration();
        try {
            Configuration.LoadData();
        } catch (Exception e) {

        }
    }

    /**
     * Gets the World Id from the configuration
     * @return UUID
     */
    public static String getWorldId() {
        return Configuration.WorldId;
    }

    public static boolean Initialize(Tools plugin) {
        Plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
            plugin.saveDefaultConfig();
        }
        plugin.getCommand("b.we.clear").setExecutor((CommandExecutor) new Clear());
        plugin.getCommand("b.we.clearhistory").setExecutor((CommandExecutor) new ClearHistory());
        plugin.getCommand("b.we.size").setExecutor((CommandExecutor) new ClipDimensions());
        plugin.getCommand("b.we.print").setExecutor((CommandExecutor) new Print());
        plugin.getCommand("b.we.select").setExecutor((CommandExecutor) new Select());

        plugin.getCommand("b.we.cut").setExecutor((CommandExecutor) new Cut());
        plugin.getCommand("b.we.copy").setExecutor((CommandExecutor) new Copy());
        plugin.getCommand("b.we.del").setExecutor((CommandExecutor) new Delete());
        plugin.getCommand("b.we.delete").setExecutor((CommandExecutor) new Delete());
        plugin.getCommand("b.we.distr").setExecutor((CommandExecutor) new Demographics());
        plugin.getCommand("b.we.paste").setExecutor((CommandExecutor) new Paste());
        plugin.getCommand("b.we.stripmine").setExecutor((CommandExecutor) new StripMine());
        plugin.getCommand("b.we.undo").setExecutor((CommandExecutor) new Undo());

        plugin.getCommand("b.reg").setExecutor((CommandExecutor) new Register());
        plugin.getCommand("b.auth").setExecutor((CommandExecutor) new Authenticate());
        plugin.getCommand("b.ls").setExecutor((CommandExecutor) new LS());
        plugin.getCommand("b.cd").setExecutor((CommandExecutor) new CD());
        plugin.getCommand("b.rm").setExecutor((CommandExecutor) new RM());
        plugin.getCommand("b.mk").setExecutor((CommandExecutor) new MK());
        plugin.getCommand("b.save").setExecutor((CommandExecutor) new SaveClipboard());
        plugin.getCommand("b.load").setExecutor((CommandExecutor) new LoadClipboard());
        plugin.getCommand("b.bbdep").setExecutor((CommandExecutor) new BlockBankDeposit());
        plugin.getCommand("b.bbinv").setExecutor((CommandExecutor) new BlockBankInventory());
        plugin.getCommand("b.bbwd").setExecutor((CommandExecutor) new BlockBankWithdrawl());
        plugin.getCommand("b.help").setExecutor((CommandExecutor) new Help());
        plugin.getCommand("b.about").setExecutor((CommandExecutor) new About());
        plugin.getCommand("b.autopickup").setExecutor((CommandExecutor) new AutoPickupToggle());
        plugin.getCommand("b.xpfly").setExecutor((CommandExecutor) new XpFly());
        
        ServerUtil.consoleLog("Calling home... no really, I am.");
        ServerUtil.consoleLog("No reason for concern, how do you think the cloud storage works?");
        try {
            Verify.Register(Plugin);
        } catch (Exception e) {
            ServerUtil.consoleLog("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ServerUtil.consoleLog("!!   Warning, cannot reach www.Blockelot.com    !!");
            ServerUtil.consoleLog("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ServerUtil.consoleLog(e);
            return false;
        }
        return true;
    }

    public static void ShutDown() {
        log.info(String.format("[%s] Disabled Version %s", PluginManager.Plugin.getDescription().getName(), PluginManager.Plugin.getDescription().getVersion()));
        Configuration.SaveData();
        PlayerInfoList.clear();
    }

}
