package com.Blockelot.worldeditor.commands.filesystem;

import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.CopyTask;
import com.Blockelot.worldeditor.commands.tasks.LoadClipboardTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoadClipboard implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player && ((player = (Player) sender).hasPermission(PluginManager.Config.Permission_FileSystem) || player.isOp())) {
            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            try {
                if (args.length != 1) {
                    player.sendMessage("Usage: /fft.load <Schematic Name>");
                    return true;
                }
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "LoadClipboard");
                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Requesting schematic load...");

                LoadClipboardTaskRequest ct = new LoadClipboardTaskRequest(pi, args[0]);
                ct.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

            } catch (Exception e) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "LoadClipboard");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }
        }
        return true;
    }

}
