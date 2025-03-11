package com.Blockelot.worldeditor.commands;

import static com.Blockelot.Configuration.Permission_BlockelotBank;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.MiscUtil;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.BlockBankDepositTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockBankDeposit
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        Material Mat;
        Mat = null;
        String MatName = "";
        if (sender instanceof Player && ((player = (Player) sender).hasPermission(Permission_BlockelotBank) || player.isOp())) {
            {
                if ("".equals(PluginManager.GetPlayerInfo(player.getUniqueId()).getLastAuth())) {
                    player.sendMessage("Please use /b.reg [email] first.");
                    return true;
                }
                MiscUtil.DumpStringArray(args);

                int Amount = 0;
                String MaterialName = "";
                try {
                    if (PluginManager.GetPlayerInfo(player.getUniqueId()).getIsProcessing()) {
                        player.sendMessage(ChatColor.RED + "Please wait for last command to finish.");
                        return true;
                    }

                    if (args.length == 0) {
                        player.sendMessage("Usage: /b.bbdep [Material] [amount]");
                        player.sendMessage("Usage: /b.bbdep all");
                        return false;
                    } else if (args.length == 1) {

                        if (args[0].trim().equalsIgnoreCase("all")) {
                            MatName = "all";
                        } else {
                            player.sendMessage("Usage: /b.bbdep all");
                            return false;
                        }
                    } else if (args.length >= 2) {
                        MaterialName = args[0];
                        try {
                            Amount = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                        }
                        if (Amount <= 0) {
                            player.sendMessage("Amount must be greater than 0");
                        }
                        try {
                            Mat = Material.getMaterial(MaterialName.trim().toUpperCase());
                        } catch (Exception e) {
                            player.sendMessage(ChatColor.RED + "Invalid material name.");
                            return true;
                        }
                        if (Mat == null) {
                            player.sendMessage(ChatColor.RED + "Invalid material name.");
                            return true;
                        }
                        if (!Mat.isBlock()) {
                            player.sendMessage(ChatColor.RED + "Only placeable materials can be deposited.");
                            return true;
                        }
                        MatName = Mat.name();
                    }

                    PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Block Bank Withdrawl");

                    if (PluginManager.HasPlayer(player)) {
                        PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                        BlockBankDepositTaskRequest task = new BlockBankDepositTaskRequest(pi, MatName, Amount);
                        task.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);
                    }

                } catch (IllegalArgumentException | IllegalStateException e) {
                    PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Copy");
                    ServerUtil.consoleLog(e.getLocalizedMessage());
                    ServerUtil.consoleLog(e.getMessage());
                    ServerUtil.consoleLog(e);
                }
            }

        }
        return true;
    }
}
