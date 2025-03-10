package com.Blockelot;
import com.Blockelot.worldeditor.commands.tasks.XpFlyRunnable;
import com.Blockelot.worldeditor.listeners.BlockListener;
import com.Blockelot.worldeditor.listeners.EntityDamageByOtherEvent;
import com.Blockelot.worldeditor.listeners.PlayerJoinListener;
import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;

public final class Tools extends JavaPlugin {

  @Override
    public void onEnable() {
        //Initialize the player join listener
        this.getServer().getPluginManager().registerEvents((Listener) new PlayerJoinListener(), (org.bukkit.plugin.Plugin) this);
        //Initialize the Block listener
        this.getServer().getPluginManager().registerEvents((Listener) new BlockListener(), (org.bukkit.plugin.Plugin) this);
        
        this.getServer().getPluginManager().registerEvents((Listener) new EntityDamageByOtherEvent(), (org.bukkit.plugin.Plugin) this);
        
        if (!PluginManager.Initialize(this)) {
            getServer().getPluginManager().disablePlugin(this);
        }
        
        
        
       new XpFlyRunnable().runTaskTimer(this, 0,  Configuration.FlyDeductTime);
        
    }

    @Override
    public void onDisable() {
        PluginManager.ShutDown();
    }
}
