package com.Blockelot.worldeditor.commands.tasks;

import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.http.RegisterResponse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RegisterTaskResponse
        extends BukkitRunnable {

    private final RegisterResponse RegisterResponse;
    private Player Player;

    public RegisterTaskResponse(RegisterResponse registerResponse, Player player) {
        this.RegisterResponse = registerResponse;
        Player = player;
    }

    @Override
    public void run() {
        if (Player != null) {
            PluginManager.GetPlayerInfo(Player.getUniqueId()).setIsProcessing(false, "Register");
            PluginManager.GetPlayerInfo(Player.getUniqueId()).setLastAuth(RegisterResponse.getAuth());
            Player.sendMessage("Registration: Message: " + this.RegisterResponse.getMessage());
        }
        this.cancel();
    }
}
