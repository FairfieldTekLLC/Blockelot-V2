package com.Blockelot.worldeditor.commands.filesystem;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.CdTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CD
        implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player;

        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Configuration.Permission_FileSystem) || player.isOp())) {

            if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                player.sendMessage("Please use /b.reg [email] first.");
                return true;
            }

            try {

                if (args.length != 1) {
                    player.sendMessage("Usage: /fft.cd <Directory>");
                    return true;
                }
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "CD");

                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());

                player.sendMessage(ChatColor.RED + "Requesting directory change.");

                new CdTaskRequest(pi, args[0]).runTaskAsynchronously((org.bukkit.plugin.Plugin) PluginManager.Plugin);

            } catch (IllegalArgumentException | IllegalStateException e) {

                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "CD");

                ServerUtil.consoleLog(e.getLocalizedMessage());

                ServerUtil.consoleLog(e.getMessage());

            }
        }
        return true;
    }
}
