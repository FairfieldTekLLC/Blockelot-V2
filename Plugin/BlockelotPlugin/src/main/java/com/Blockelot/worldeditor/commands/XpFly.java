/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.commands;

import com.Blockelot.PluginManager;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.Blockelot.Configuration;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

/**
 *
 * @author geev
 */
public class XpFly implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player1) {

            if (player1.getGameMode() == GameMode.CREATIVE) {
                return true;
            }

            if (player1.hasPermission(Configuration.Permission_XpFly)) {
                PlayerInfo pi = PluginManager.GetPlayerInfo(player1.getUniqueId());
                if (pi.IsFlying) {
                    pi.IsFlying = false;
                    player1.setAllowFlight(false);
                    player1.setFlying(false);
                    player1.sendMessage(ChatColor.BLUE + "-----------------Flying Disabled---------------------");
                } else {
                    pi.IsFlying = true;
                    player1.setAllowFlight(true);
                    player1.setFlying(true);
                    Vector vec = new Vector(0, 1, 0);
                    player1.setVelocity(vec);
                    player1.sendMessage(ChatColor.BLUE + "-----------------Flying Enabled---------------------");
                }
            }
        }
        return true;
    }
}
