package com.Blockelot.worldeditor.commands.tasks;

import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.http.AuthenticateResponse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/*
This function handles the authentication response from the server.

 */
public class AuthenticateTaskResponse
        extends BukkitRunnable {

    private final AuthenticateResponse AuthenticateResponse;

    public AuthenticateTaskResponse(AuthenticateResponse authenticateResponse) {
        this.AuthenticateResponse = authenticateResponse;
    }

    @Override
    public void run() {
        Player player = PluginManager.Plugin.getServer().getPlayer(UUID.fromString(this.AuthenticateResponse.getUuid()));
        try {

            if (player != null) {
                if (!AuthenticateResponse.getMessage().equalsIgnoreCase("")) {
                    player.sendMessage("Registration: Message: " + this.AuthenticateResponse.getMessage());
                }

            }
        } catch (Exception e) {
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
        }
        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Authenticate");
        this.cancel();
    }
}
