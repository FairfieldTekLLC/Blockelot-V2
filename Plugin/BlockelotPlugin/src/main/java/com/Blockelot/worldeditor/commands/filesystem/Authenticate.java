package com.Blockelot.worldeditor.commands.filesystem;

import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.AuthenticateTaskRequest;
import com.Blockelot.worldeditor.commands.tasks.LoginTaskRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Authenticate
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player && ((player = (Player) sender).hasPermission(PluginManager.Config.Permission_FileSystem) || player.isOp())) {

            try {
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }

                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Authenticate");

                if (args.length == 1) {

                    PluginManager.GetPlayerInfo(player.getUniqueId()).setLastAuth(args[0]);

                    player.sendMessage("Processing Login....");

                    new LoginTaskRequest(PluginManager.GetPlayerInfo(player.getUniqueId()), args[0]).runTaskAsynchronously((org.bukkit.plugin.Plugin) PluginManager.Plugin);

                    return true;

                }

                new AuthenticateTaskRequest(PluginManager.GetPlayerInfo(player.getUniqueId())).runTaskAsynchronously((org.bukkit.plugin.Plugin) PluginManager.Plugin);

                player.sendMessage("Requesting Authenticating against Library...");

                //player.sendMessage("After email use: " + ChatColor.YELLOW + "'/fft.auth <Auth Token>'" + ChatColor.WHITE + " to login.");
            } catch (Exception e) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Authenticate");

                ServerUtil.consoleLog(e.getLocalizedMessage());

                ServerUtil.consoleLog(e.getMessage());
            }
        }
        return true;
    }
}
