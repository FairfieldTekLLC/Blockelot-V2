package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.GriefPreventionUtil;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.CutTask;
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

public class Cut
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_Cut)
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

                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Cut");
                PluginManager.GetPlayerInfo(player.getUniqueId()).TurnOffSelectionBox();

                if (PluginManager.HasPlayer(player)) {
                    int sbx;
                    int sez;
                    int sby;
                    int sex;
                    int sbz;
                    int sey;
                    PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                    if (pi.getSelectStart() == null || pi.getSelectEnd() == null) {
                        player.sendMessage("Starting and Ending Coordinates not set.  Use /fft.we.select x y z ");
                        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Cut");
                        return true;
                    }
                    if (pi.getSelectStart().X > pi.getSelectEnd().X) {
                        sbx = pi.getSelectEnd().X;
                        sex = pi.getSelectStart().X;
                    } else {
                        sex = pi.getSelectEnd().X;
                        sbx = pi.getSelectStart().X;
                    }
                    if (pi.getSelectStart().Y > pi.getSelectEnd().Y) {
                        sby = pi.getSelectEnd().Y;
                        sey = pi.getSelectStart().Y;
                    } else {
                        sey = pi.getSelectEnd().Y;
                        sby = pi.getSelectStart().Y;
                    }
                    if (pi.getSelectStart().Z > pi.getSelectEnd().Z) {
                        sbz = pi.getSelectEnd().Z;
                        sez = pi.getSelectStart().Z;
                    } else {
                        sez = pi.getSelectEnd().Z;
                        sbz = pi.getSelectStart().Z;
                    }
                    
                    if (!GriefPreventionUtil.IsPlayerOwner(player, sbx, sex, sby, sey, sbz, sez))
                    {
                        player.sendMessage(ChatColor.RED + "Cut aborted, you do not have a claim.");   
                        return false;
                    }
                    
                    player.sendMessage(ChatColor.RED + "Starting Cut Procedure...");

                    CutTask ct = new CutTask(sbx, sex, sby, sey, sbz, sez, player.getUniqueId());

                    ct.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Cut");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }
        }

        return true;
    }
}
