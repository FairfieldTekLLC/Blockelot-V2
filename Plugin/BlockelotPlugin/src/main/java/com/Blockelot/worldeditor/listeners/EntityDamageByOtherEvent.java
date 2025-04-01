/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.listeners;

import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 *
 * @author geev
 */

public class EntityDamageByOtherEvent implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow) {
            if (event.getEntity() instanceof Player player) {
                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                if (pi.IsFlying) {
                    //pi.IsFlying = false;
                    //player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage("You've been shot down!");
                }
            }
        }
    }
}
