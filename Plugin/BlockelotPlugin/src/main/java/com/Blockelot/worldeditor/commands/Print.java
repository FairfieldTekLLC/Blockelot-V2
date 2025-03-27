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

            if (info.getSelectStart() != null) {
                player.sendMessage("Start Position : " + info.getSelectStart().toString());
            } else {
                player.sendMessage("Starting Position not defined.");
            }
            if (info.getSelectEnd() != null) {
                player.sendMessage("End Position : " + info.getSelectEnd().toString());
            } else {
                player.sendMessage("Ending Position not defined.");
            }
        }
        return true;
    }
}
