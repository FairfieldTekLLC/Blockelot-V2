package com.Blockelot.worldeditor.commands.filesystem;

import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.CopyTask;
import com.Blockelot.worldeditor.commands.tasks.SaveClipboardTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveClipboard
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player && ((player = (Player) sender).hasPermission(PluginManager.Config.Permission_FileSystem) || player.isOp())) {
            try {
                if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                    player.sendMessage("Please use /b.reg [email] first.");
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage("Usage: /fft.Save <Schematic Name>");
                    return true;
                }
                if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                    player.sendMessage("Please wait for last command to finish.");
                    return true;
                }
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "SaveClipboard");

                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());

                if (pi.ClipSchematic.IsEmpty()) {
                    player.sendMessage("No blocks in Clipboard.");
                    PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "SaveClipboard");
                }

                player.sendMessage(ChatColor.RED + "Requesting schematic save...");
                
                
                //new SaveClipboardTaskRequest(pi, args[0]).runTaskAsynchronously((org.bukkit.plugin.Plugin) PluginManager.Plugin);
                
                
                
                    SaveClipboardTaskRequest ct = new SaveClipboardTaskRequest(pi,args[0]);

                    ct.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

                
            } catch (Exception e) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "SaveClipboard");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }
        }
        return true;
    }
}
