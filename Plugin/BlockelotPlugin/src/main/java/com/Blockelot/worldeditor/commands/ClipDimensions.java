package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author geev
 */
public class ClipDimensions implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Size)
                || (player = (Player) sender).hasPermission(Configuration.Permission_User) || player.isOp())) {
            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                player.sendMessage("Please wait for last command to finish.");
                return true;
            }

            PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
            if (pi.getSelectStart() == null || pi.getSelectEnd() == null) {
                player.sendMessage(ChatColor.YELLOW + "Please select something first!");
                return true;
            }

            int x = Math.abs(pi.getSelectStart().X - pi.getSelectEnd().X);
            int y = Math.abs(pi.getSelectStart().Y - pi.getSelectEnd().X);
            int z = Math.abs(pi.getSelectStart().Z - pi.getSelectEnd().Z);

            player.sendMessage(ChatColor.YELLOW + "Your selection dimension is " + x + " " + y + " " + z + ".");

            return true;
        }
        return true;
    }
}
