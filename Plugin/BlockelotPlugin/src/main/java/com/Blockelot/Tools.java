package com.Blockelot;

import static com.Blockelot.Configuration.BaseUri;
import static com.Blockelot.Configuration.FlyDeductTime;
import static com.Blockelot.Configuration.IncludeInventoryWhenPasting;
import static com.Blockelot.Configuration.MaxBlocksReadPerTick;
import static com.Blockelot.Configuration.MaxBlocksUploadPerCall;
import static com.Blockelot.Configuration.MaxBlocksWritePerTick;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.Runnable.SelectionHighlightRunnable;
import com.Blockelot.worldeditor.listeners.BlockListener;
import com.Blockelot.worldeditor.listeners.EntityDamageByOtherEvent;
import com.Blockelot.worldeditor.listeners.PlayerJoinListener;
import com.Blockelot.worldeditor.listeners.PlayerMoveEvents;
import com.griefdefender.api.GriefDefender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Tools extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getConfig().addDefault("settings.config.MaxBlocksWritePerTick", MaxBlocksWritePerTick);
        this.getConfig().addDefault("settings.config.MaxBlocksReadPerTick", MaxBlocksReadPerTick);
        this.getConfig().addDefault("settings.config.MaxBlocksUploadPerCall", MaxBlocksUploadPerCall);
        this.getConfig().addDefault("settings.config.IncludeInventoryWhenPasting", IncludeInventoryWhenPasting);
        this.getConfig().addDefault("settings.config.baseuri", BaseUri);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        //Initialize the player join listener
        this.getServer().getPluginManager().registerEvents((Listener) new PlayerJoinListener(), (org.bukkit.plugin.Plugin) this);
        //Initialize the Block listener
        this.getServer().getPluginManager().registerEvents((Listener) new BlockListener(), (org.bukkit.plugin.Plugin) this);
        this.getServer().getPluginManager().registerEvents((Listener) new EntityDamageByOtherEvent(), (org.bukkit.plugin.Plugin) this);
        this.getServer().getPluginManager().registerEvents((Listener) new PlayerMoveEvents(), (org.bukkit.plugin.Plugin) this);
        new XpFlyRunnable().runTaskTimer(this, 0, FlyDeductTime);
        //new SelectionHighlightRunnable().runTaskTimer(this,0,30);
        /*
        Used to initialize the GreifDefender plugin.
        */
        GriefDefender gd;
        
        //Find the plugin
        Plugin p =  this.getServer().getPluginManager().getPlugin("GriefDefender");
        
        if (p==null)
        {
              ServerUtil.consoleLog("Is GriefDefender installed?");
        }
        else
        {
              ServerUtil.consoleLog("GriefDefender installed.");
              PluginManager.GriefDefenderLoaded=true;
              gd = new GriefDefender();
        }
        
        if (!PluginManager.Initialize(this)) {
            getServer().getPluginManager().disablePlugin(this);
        }
        
        
        

    }

    @Override
    public void onDisable() {
        PluginManager.ShutDown();
        saveConfig();
    }

    public void setAndSave(String path, Object value) {
        FileConfiguration config = getConfig();
        config.set(path, value);
        saveConfig();
    }

}
