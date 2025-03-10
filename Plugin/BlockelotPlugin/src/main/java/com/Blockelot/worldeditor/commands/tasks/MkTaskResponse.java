package com.Blockelot.worldeditor.commands.tasks;

import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.http.MkResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MkTaskResponse
        extends BukkitRunnable {

    public MkResponse MkResponse;

    public MkTaskResponse(MkResponse mkResponse) {
        this.MkResponse = mkResponse;
    }

    @Override
    public void run() {
        Player player = PluginManager.Plugin.getServer().getPlayer(UUID.fromString(this.MkResponse.getUuid()));
        try {

            if (player == null) {
                return;
            }

            PluginManager.GetPlayerInfo(player.getUniqueId()).setLastAuth(this.MkResponse.getAuth());
            PluginManager.GetPlayerInfo(player.getUniqueId()).setCurrentPath(this.MkResponse.getDirectoryPath());
            if (!this.MkResponse.getWasSuccessful()) {
                player.sendMessage(ChatColor.RED + this.MkResponse.getMessage());
            } else {
                player.sendMessage("Directory created.");
            }
            player.sendMessage("Current Directory: " + this.MkResponse.getDirectoryPath());
        } catch (Exception e) {

        }
        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "MK");
        this.cancel();
    }

}
