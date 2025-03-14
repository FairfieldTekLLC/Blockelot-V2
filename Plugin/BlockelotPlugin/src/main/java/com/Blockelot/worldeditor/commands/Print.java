package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Print
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Print)
                || (player = (Player) sender).hasPermission(Configuration.Permission_User) || player.isOp())) {
            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            PlayerInfo info = PluginManager.GetPlayerInfo(player.getUniqueId());
            if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                player.sendMessage("Please wait for last command to finish.");
                return true;
            }

            if (info.SelectStart != null) {
                player.sendMessage("Start Position : " + info.SelectStart.toString());
            } else {
                player.sendMessage("Starting Position not defined.");
            }
            if (info.SelectEnd != null) {
                player.sendMessage("End Position : " + info.SelectEnd.toString());
            } else {
                player.sendMessage("Ending Position not defined.");
            }
        }
        return true;
    }
}
