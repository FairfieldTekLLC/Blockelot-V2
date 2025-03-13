package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Clear
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Clear) || (player = (Player) sender).hasPermission(Configuration.Permission_User) || player.isOp())) {
            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                player.sendMessage("Please wait for last command to finish.");
                return true;
            }
            PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
            pi.SelectStart = null;
            pi.SelectEnd = null;
            pi.ClipSchematic.Clear();
            player.sendMessage(ChatColor.RED + "Cleared Selection.");
            return true;
        }
        return true;

    }
}
