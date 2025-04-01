/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.listeners;

import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

/**
 *
 * @author geev
 */
public class PlayerMoveEvents implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
        LivingEntity le = (LivingEntity) player;
        if (player.getVelocity().getY() > 0) {
            if (!le.isClimbing() && le.isOnGround() ) {
                if (pi.IsFlying) {
                    Vector vec = new Vector(0, 1, 0);
                    player.setVelocity(vec);
                }
            }
        }
    }

}
