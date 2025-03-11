package com.Blockelot.worldeditor.commands;


import static com.Blockelot.Configuration.Permission_BlockelotBank;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.BlockBankInventoryTaskRequest;
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
public class BlockBankInventory implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Permission_BlockelotBank) || player.isOp())) {
            {
                if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                    player.sendMessage("Please use /b.reg [email] first.");
                    return true;
                }
                try {
                    if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                        player.sendMessage(ChatColor.RED + "Please wait for last command to finish.");
                        return true;
                    }
                    PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Block Bank Inventory");

                    if (PluginManager.HasPlayer(player)) {
                        PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());

                        String criteria = "";
                        
                        if (args.length==1)
                        {
                            criteria = args[0];
                        }
                        else
                        {
                            criteria = "";
                        }
                            BlockBankInventoryTaskRequest task = new BlockBankInventoryTaskRequest(pi,criteria);
                            task.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

                        
                        
                    }

                } catch (IllegalArgumentException | IllegalStateException e) {
                    PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "bbinv");
                    ServerUtil.consoleLog(e.getLocalizedMessage());
                    ServerUtil.consoleLog(e.getMessage());
                }
            }
        }

        return true;
    }
}
