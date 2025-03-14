package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.http.RegisterResponse;
import com.Blockelot.worldeditor.http.RmRequest;
import com.Blockelot.worldeditor.http.RmResponse;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.JsonSyntaxException;

public class RmTaskRequest
        extends HttpRequestor {

    private final String Target;
    private PlayerInfo PlayerInfo;

    public RmTaskRequest(PlayerInfo pi, String target) {
        this.Target = target;
        PlayerInfo = pi;
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "RM");
            Gson gson = new Gson();
            RmRequest rmRequest = new RmRequest();
            rmRequest.setAuth(PlayerInfo.getLastAuth());
            rmRequest.setCurrentDirectory(PlayerInfo.getCurrentPath());
            rmRequest.setUuid(PlayerInfo.getUUID());
            rmRequest.setTargetDirectory(this.Target);
            String body = gson.toJson(rmRequest);
            RmResponse response = gson.fromJson(RequestHttp(Configuration.BaseUri + "DirRm", body),
                    RmResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());
            response.setUuid(PlayerInfo.getUUID());
            new RmTaskResponse(response).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setMessage("An Error has occurred.");
            registerResponse.setWasSuccessful(false);
            registerResponse.setUuid(PlayerInfo.getUUID());
            new RegisterTaskResponse(registerResponse, PlayerInfo.getPlayer()).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
    }
}
