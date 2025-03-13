package com.Blockelot.worldeditor.listeners;

import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.commands.tasks.AuthenticateTaskRequest;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener
        implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PluginManager.HasPlayer(player)) {

            ServerUtil.consoleLog("Adding Player " + player.getUniqueId().toString() + " - joined.  ");

            PluginManager.AddPlayerInfo(new PlayerInfo(player));

            new AuthenticateTaskRequest(PluginManager.GetPlayerInfo(player.getUniqueId())).runTaskAsynchronously((org.bukkit.plugin.Plugin) PluginManager.Plugin);

        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (PluginManager.HasPlayer(player)) {

            ServerUtil.consoleLog("Removing Player " + player.getUniqueId().toString() + " - left.  ");

            player.setAllowFlight(false);
            player.setFlying(false);

            PluginManager.RemovePlayer(player);

        } else {
            ServerUtil.consoleLog("Couldn't find player...");
        }
    }

}
