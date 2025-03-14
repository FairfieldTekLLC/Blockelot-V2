package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.CopyTask;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Copy
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Copy)
                || (player = (Player) sender).hasPermission(Configuration.Permission_Editor) || player.isOp())) {
            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }
            try {

                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }

                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Copy");

                if (PluginManager.HasPlayer(player)) {
                    int sbx;
                    int sez;
                    int sby;
                    int sex;
                    int sbz;
                    int sey;
                    PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                    if (pi.SelectStart == null || pi.SelectEnd == null) {
                        player.sendMessage("Starting and Ending Coordinates not set.  Use /fft.we.select x y z ");
                        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Copy");
                        return true;
                    }
                    if (pi.SelectStart.X > pi.SelectEnd.X) {
                        sbx = pi.SelectEnd.X;
                        sex = pi.SelectStart.X;
                    } else {
                        sex = pi.SelectEnd.X;
                        sbx = pi.SelectStart.X;
                    }
                    if (pi.SelectStart.Y > pi.SelectEnd.Y) {
                        sby = pi.SelectEnd.Y;
                        sey = pi.SelectStart.Y;
                    } else {
                        sey = pi.SelectEnd.Y;
                        sby = pi.SelectStart.Y;
                    }
                    if (pi.SelectStart.Z > pi.SelectEnd.Z) {
                        sbz = pi.SelectEnd.Z;
                        sez = pi.SelectStart.Z;
                    } else {
                        sez = pi.SelectEnd.Z;
                        sbz = pi.SelectStart.Z;
                    }
                    player.sendMessage(ChatColor.RED + "Starting Copy Procedure...");

                    CopyTask ct = new CopyTask(sbx, sex, sby, sey, sbz, sez, player.getUniqueId());

                    ct.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Copy");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }
        }

        return true;
    }
}
