/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.Runnable;

import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author geev
 */
public class SelectionHighlightRunnable  extends BukkitRunnable {

    @Override
    public void run() {
        Set<Entry<UUID, PlayerInfo>> entrySet = PluginManager.PlayerInfoList.entrySet();
                
        Iterator<Entry<UUID, PlayerInfo>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PlayerInfo> entry = iterator.next();
            try
            {
                PlayerInfo pi = entry.getValue();
                //pi.DrawParticlesAroundSelect();
            }
            catch (Exception e)
            {
                
            }

        }
    }
    
}
