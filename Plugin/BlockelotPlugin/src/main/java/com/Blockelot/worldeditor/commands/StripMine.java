package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.commands.tasks.StripMineTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.command.CommandExecutor;

/**
 *
 * @author geev
 */
public class StripMine implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        boolean deposit = false;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Editor) || 
                (player = (Player) sender).hasPermission(Configuration.Permission_StripMine) || player.isOp())) {
            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                player.sendMessage("Please wait for last command to finish.");
                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("true")) {
                    if (PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth().equalsIgnoreCase("")) {
                        deposit = false;
                        player.sendMessage("Player not linked to Blockelot, canceling deposit.");
                    } else {
                        player.sendMessage("Player linked, depositing blocks in bank.");
                        deposit = true;
                    }
                }
            }

            PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "StripMine");

            StripMineTask ut = new StripMineTask(PluginManager.GetPlayerInfo(player.getUniqueId()), deposit);

            ut.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 1, 15);
        }
        return true;

    }
}
