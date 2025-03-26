/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.Util;

import com.Blockelot.PluginManager;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

/**
 *
 * @author geev
 */
public class GriefPreventionUtil {
    
    
    public static boolean IsPlayerOwner(Player player, int bx, int ex, int by, int ey, int bz, int ez)
    {
        ServerUtil.consoleLog("Checking range " + bx + "<" + ex + " " + by + "<" + ey + " " + bz + "<" + ez);
         if (!PluginManager.GriefDefenderLoaded)
            return true;
        for (int x = bx;x<=ex;x++)
            for (int y = by;y<ey;y++)
                for (int z = bz;z<=ez;z++)
                {
                Location loc = new Location(player.getWorld(),x,y,z);
                final Claim claim = GriefDefender.getCore().getClaimAt(loc);
                if (claim.getOwnerUniqueId().toString() == null ? player.getUniqueId().toString() != null : !claim.getOwnerUniqueId().toString().equals(player.getUniqueId().toString()))
                    {
                    return false;
                    }
                }
        return true;
    }
    
    
    
    public static boolean IsPlayerOwner(Player player, int x, int y, int z)
    {
        if (!PluginManager.GriefDefenderLoaded)
            return true;
        
        Location loc = new Location(player.getWorld(),x,y,z);
        final Claim claim = GriefDefender.getCore().getClaimAt(loc);
        if (claim.getOwnerUniqueId().toString() == null ? player.getUniqueId().toString() != null : !claim.getOwnerUniqueId().toString().equals(player.getUniqueId().toString()))
            {
            return false;
            }
        
        return true;
    }
    
    public static boolean CheckChunkForClaimOwner(Player player, int xx, int yy, int zz)
    {        
    if (!PluginManager.GriefDefenderLoaded)
        return true;
    
            Location loc = new Location(player.getWorld(),xx,yy,zz);
            Chunk chunk = player.getWorld().getChunkAt(loc);
            final int minX = chunk.getX() << 4;
            final int minZ = chunk.getZ() << 4;
            final int maxX = minX | 15;
            final int maxY = chunk.getWorld().getMaxHeight()-1;
            final int maxZ = minZ | 15;

            for (int x = minX; x <= maxX; ++x) {
                for (int y = 0; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        Location target = new Location(player.getWorld(),x,y,z);
                        
                        final Claim claim = GriefDefender.getCore().getClaimAt(target);
                        if (claim == null)
                            {
                            return false;
                            }
                        if (claim.getOwnerUniqueId().toString() == null ? player.getUniqueId().toString() != null : !claim.getOwnerUniqueId().toString().equals(player.getUniqueId().toString()))
                            {
                            return false;
                            }
                        }
                    }
                }
        
        return true;
}
    private static final Logger LOG = getLogger(GriefPreventionUtil.class.getName());
}