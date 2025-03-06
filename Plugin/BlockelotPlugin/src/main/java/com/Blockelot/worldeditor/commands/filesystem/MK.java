package com.Blockelot.worldeditor.commands.filesystem;

import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.MkTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MK
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player && ((player = (Player) sender).hasPermission(PluginManager.Config.Permission_FileSystem) || player.isOp())) {
            try {
                if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                    player.sendMessage("Please use /b.reg [email] first.");
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage("Usage: /fft.mk <Directory>");
                    return true;
                }
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Mk");
                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Requesting directory creation.");
                new MkTaskRequest(PluginManager.GetPlayerInfo(player.getUniqueId()), args[0]).runTaskAsynchronously((org.bukkit.plugin.Plugin) PluginManager.Plugin);
            } catch (Exception e) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Mk");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }
        }
        return true;
    }
}
