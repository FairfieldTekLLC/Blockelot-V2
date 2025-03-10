package com.Blockelot.worldeditor.commands.tasks;

import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.Blockelot.worldeditor.http.LoginResponse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginTaskResponse
        extends BukkitRunnable {

    private final LoginResponse LoginResponse;

    public LoginTaskResponse(LoginResponse loginResponse) {
        this.LoginResponse = loginResponse;
    }

    @Override
    public void run() {
        Player player = PluginManager.Plugin.getServer().getPlayer(UUID.fromString(this.LoginResponse.getUuid()));

        try {
            if (this.LoginResponse == null || this.LoginResponse.getUuid() == null) {
                return;
            }

            if (player != null) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Login");
                PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());
                if (this.LoginResponse.getIsAuthorized()) {
                    pi.setLastAuth(this.LoginResponse.getAuth());
                    pi.setCurrentPath(this.LoginResponse.getCurrentPath());
                    if (LoginResponse.getMessage().length() > 0) {
                        player.sendMessage(this.LoginResponse.getMessage());
                    }
                    player.sendMessage("Current Remote Directory: " + pi.getCurrentPath());
                } else {
                    player.sendMessage("Login Failed: Bad Auth Code.");
                    pi.setLastAuth("");
                    pi.setCurrentPath("");
                }

            }
        } catch (Exception e) {
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
        }
        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Login");
        this.cancel();
    }
}
