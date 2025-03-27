package com.Blockelot.worldeditor.commands;

import com.Blockelot.Configuration;
import static com.Blockelot.Configuration.Permission_Delete;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.GriefPreventionUtil;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.DeleteTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Delete
        implements CommandExecutor {

                   int sbx;
                    int sez;
                    int sby;
                    int sex;
                    int sbz;
                    int sey;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Permission_Delete)
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
                PluginManager.GetPlayerInfo(player.getUniqueId()).TurnOffSelectionBox();
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Delete");

                if (!PluginManager.HasPlayer(player)) {
                    player.sendMessage("Select something first!");
                }
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getSelectStart() == null || PluginManager.GetPlayerInfo(player.getUniqueId()).getSelectEnd() == null) {
                    player.sendMessage("Select something with /fft.we.select");
                }
                player.sendMessage(ChatColor.RED + "Starting Delete Procedure...");

                var pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                if (pi.getSelectEnd() == null || pi.getSelectStart() == null) {
                    return false;
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
                if (!GriefPreventionUtil.IsPlayerOwner(player, sbx, sex, sby, sey, sbz, sez)){
                    player.sendMessage("You do not have a claim for the selected area");
                    PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Delete");
                    return false;
                }

                DeleteTask ut = new DeleteTask(sbx, sex, sby, sey, sbz, sez,player);

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
