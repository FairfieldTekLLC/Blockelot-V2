/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import static com.Blockelot.Configuration.FlyXpPrice;
import com.Blockelot.PluginManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author geev
 */
public class XpFlyRunnable extends BukkitRunnable {

    @Override
    public void run() {
     if (PluginManager.PlayerInfoList.isEmpty())
         return;
            PluginManager.PlayerInfoList.entrySet().stream().map(entry -> entry.getValue()).forEachOrdered(pi -> {
             Player player = pi.getPlayer();
            if (pi.IsFlying && player.getGameMode() != GameMode.CREATIVE )
            {
                if (player.isFlying())
                {
                
                try
                {
                    player.setExp(player.getExp() - (float)FlyXpPrice);
                }
                catch (Exception e)
                {           
                    player.setExp(0f);
                }
                
                if (player.getExp() <= 0.0F && player.getLevel() >= 1) 
                    {
                    player.setLevel(player.getLevel() - 1);
                    player.setExp(1.0F);
                    }
            
                if (player.getLevel()==0)
                {
                    player.sendMessage("Warning you are low on Experience.....");
                }
            
                if (player.getExp() == 0.0f && player.getLevel() == 0)
                    {
                    player.sendMessage("You are out of experience.... Flying turned off.");
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    pi.IsFlying=false;
                    }
            }}
        });
    }
    
}
