/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.commands;

import static com.Blockelot.PluginManager.GetPlayerInfo;
import com.Blockelot.worldeditor.container.PlayerInfo;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoPickupToggle implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            if (player.hasPermission("Blockelot.Player.AutoPickup")) {
                PlayerInfo pi = GetPlayerInfo(player.getUniqueId());
                if (pi.AutoPickup) {
                    player.sendMessage("Autopickup is off.");
                    pi.AutoPickup = false;
                } else {
                    player.sendMessage("Autopickup is on.");
                    pi.AutoPickup = true;
                }
            }
        }
        return true;
    }
    private static final Logger LOG = Logger.getLogger(AutoPickupToggle.class.getName());
}
