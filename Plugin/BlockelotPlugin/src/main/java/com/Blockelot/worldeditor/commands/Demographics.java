package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;
import com.Blockelot.Util.MiscUtil;

public class Demographics implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Distr) ||
                (player = (Player) sender).hasPermission(Configuration.Permission_Editor) || player.isOp())) {
             if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                player.sendMessage("Please wait for last command to finish.");
                return true;
            }
           PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Demographics");
            PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
            if (pi.ClipSchematic.Size() > 0) {
                player.sendMessage("Materials in your clipboard are:");
                player.sendMessage("================================");
                HashMap<String, Integer> results = pi.ClipSchematic.GetBlockMaterialCounts();
                for (Map.Entry<String, Integer> entry : results.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    //100000
                    player.sendMessage("--->" + ChatColor.YELLOW + MiscUtil.padLeft(Integer.toString(value), 6, "0") + "   " + key);
                    // ...
                }
                player.sendMessage("===========Finished=============");
            }

            PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Demographics");
        }
        return true;
    }

}
