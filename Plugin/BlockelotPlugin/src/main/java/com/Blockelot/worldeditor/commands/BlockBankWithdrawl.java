package com.Blockelot.worldeditor.commands;

import static com.Blockelot.Configuration.Permission_BlockelotBank;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.BlockBankWithDrawlTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author geev
 */
public class BlockBankWithdrawl implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Permission_BlockelotBank) || player.isOp())) {
            try {
                if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                    player.sendMessage("Please use /b.reg [email] first.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("Usage: /b.bbwd [Material] [amount]");
                    return false;
                }
                String MaterialName = args[0];
                int Amount = Integer.parseInt(args[1]);

                if (Amount <= 0) {
                    player.sendMessage("Amount must be greater than 0");
                }

                Material Mat = null;

                try {
                    Mat = Material.getMaterial(MaterialName.toUpperCase().trim());
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid material name.");
                    return true;
                }

                if (Mat == null) {
                    player.sendMessage(ChatColor.RED + "Invalid material name.");
                    return true;
                }

                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                if (!Mat.isBlock()) {
                    player.sendMessage(ChatColor.RED + "Only placeable materials can be deposited.");
                    return true;
                }
                BlockBankWithDrawlTaskRequest task = new BlockBankWithDrawlTaskRequest(pi, Mat, Amount);
                task.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

            } catch (IllegalArgumentException | IllegalStateException e) {
               PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Copy");
                ServerUtil.consoleLog(e.getLocalizedMessage());
                ServerUtil.consoleLog(e.getMessage());
            }
        }

        return true;
    }
}
