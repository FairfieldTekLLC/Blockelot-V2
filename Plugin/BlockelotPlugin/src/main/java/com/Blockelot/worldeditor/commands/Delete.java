package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.DeleteTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Delete
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Delete) || 
                (player = (Player) sender).hasPermission(Configuration.Permission_Editor) || player.isOp())) {
             if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            try {
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Delete");

                if (!PluginManager.HasPlayer(player)) {
                    player.sendMessage("Select something first!");
                }
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).SelectStart == null || PluginManager.GetPlayerInfo(player.getUniqueId()).SelectEnd == null) {
                    player.sendMessage("Select something with /fft.we.select");
                }
                player.sendMessage(ChatColor.RED + "Starting Delete Procedure...");
                DeleteTask ut = new DeleteTask(player);

                ut.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 1, 15);

            } catch (IllegalArgumentException | IllegalStateException e) {
               PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Delete");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }

        }
        return true;
    }
}
