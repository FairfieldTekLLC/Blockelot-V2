package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.http.RegisterRequest;
import com.Blockelot.worldeditor.http.RegisterResponse;
import com.google.gson.JsonSyntaxException;
import org.bukkit.entity.Player;

public class RegisterTaskRequest
        extends HttpRequestor {

    private final String Uuid;
    private final String EmailAddress;
    private Player Player;

    public RegisterTaskRequest(Player player, String emailAddress) {
        this.Uuid = player.getUniqueId().toString();
        this.EmailAddress = emailAddress;
        Player = player;
    }

    @Override
    public void run() {
        try {
            PluginManager.GetPlayerInfo(Player.getUniqueId()).setIsProcessing(true, "Register");
            Gson gson = new Gson();
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmailAddress(this.EmailAddress);
            registerRequest.setUuid(this.Uuid);
            String body = gson.toJson(registerRequest);
            RegisterResponse registerResponse = gson.fromJson(RequestHttp(Configuration.BaseUri + "Register", body),
                    RegisterResponse.class);
            PluginManager.GetPlayerInfo(Player.getUniqueId()).setLastAuth(registerResponse.getAuth());

            new RegisterTaskResponse(registerResponse, Player.getPlayer()).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
            ServerUtil.consoleLog(e);
            PluginManager.GetPlayerInfo(Player.getUniqueId()).setIsProcessing(false, "Register");
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setMessage("An Error has occurred.");
            registerResponse.setWasSuccessful(false);
            registerResponse.setUuid(this.Uuid);
            new RegisterTaskResponse(registerResponse, Player).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
    }
}
