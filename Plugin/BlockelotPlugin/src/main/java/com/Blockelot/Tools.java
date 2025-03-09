package com.Blockelot;
import com.Blockelot.worldeditor.listeners.BlockListener;
import com.Blockelot.worldeditor.listeners.PlayerJoinListener;
import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;

public final class Tools extends JavaPlugin {

  @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener) new PlayerJoinListener(), (org.bukkit.plugin.Plugin) this);
        this.getServer().getPluginManager().registerEvents((Listener) new BlockListener(), (org.bukkit.plugin.Plugin) this);
        if (!PluginManager.Initialize(this)) {
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        PluginManager.ShutDown();
    }
}
