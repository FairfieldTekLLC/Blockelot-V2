package com.Blockelot;

import org.bukkit.plugin.java.JavaPlugin;
import com.Blockelot.worldeditor.listeners.PlayerJoinListener;
import com.Blockelot.worldeditor.listeners.ChunkEvents;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.Chunk;

import org.bukkit.plugin.java.JavaPlugin;

public final class Tools extends JavaPlugin {

  @Override
    public void onEnable() {
//        try {
        this.getServer().getPluginManager().registerEvents((Listener) new PlayerJoinListener(), (org.bukkit.plugin.Plugin) this);
        if (!PluginManager.Initialize(this)) {

            getServer().getPluginManager().disablePlugin(this);
        }
//        } catch (Exception ex) {
//            //Caused to crash the loader.
//            PluginManager.ShutDown();
//            Chunk chunk = (Chunk)this.getServer();
//            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void onDisable() {
        PluginManager.ShutDown();
    }
}
