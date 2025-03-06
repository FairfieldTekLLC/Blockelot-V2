package com.Blockelot.worldeditor.commands.tasks;

import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.http.RmResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RmTaskResponse
        extends BukkitRunnable {

    public RmResponse RmResponse;

    public RmTaskResponse(RmResponse rmResponse) {
        this.RmResponse = rmResponse;
    }

    @Override
    public void run() {
        Player player = PluginManager.Plugin.getServer().getPlayer(UUID.fromString(this.RmResponse.getUuid()));
        try {
            try {
                if (player == null) {
                    return;
                }

                PluginManager.GetPlayerInfo(player.getUniqueId()).setLastAuth(this.RmResponse.getAuth());
                PluginManager.GetPlayerInfo(player.getUniqueId()).setCurrentPath(this.RmResponse.getDirectoryPath());

                if (!this.RmResponse.getWasSuccessful()) {
                    player.sendMessage(ChatColor.RED + this.RmResponse.getMessage());
                } else {
                    player.sendMessage(ChatColor.YELLOW + this.RmResponse.getMessage());
                }
                player.sendMessage("Current Directory: " + this.RmResponse.getDirectoryPath());
            } catch (Exception e) {
            }
        } catch (Exception e) {
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
        }

        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Rm");
        this.cancel();
    }
}
