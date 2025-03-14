package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.http.MkRequest;
import com.Blockelot.worldeditor.http.MkResponse;
import com.Blockelot.worldeditor.http.RegisterResponse;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.JsonSyntaxException;

public class MkTaskRequest
        extends HttpRequestor {

    private final String Target;
    private final PlayerInfo PlayerInfo;

    public MkTaskRequest(PlayerInfo pi, String target) {
        PlayerInfo = pi;
        this.Target = target;
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "MK");
            Gson gson = new Gson();
            MkRequest mkRequest = new MkRequest();
            mkRequest.setAuth(PlayerInfo.getLastAuth());
            mkRequest.setCurrentDirectory(PlayerInfo.getCurrentPath());
            mkRequest.setUuid(PlayerInfo.getUUID());
            mkRequest.setTargetDirectory(this.Target);
            String body = gson.toJson(mkRequest);
            MkResponse response = gson.fromJson(RequestHttp(Configuration.BaseUri + "DirMk", body), MkResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());
            response.setUuid(PlayerInfo.getUUID());
            new MkTaskResponse(response).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);

        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setMessage("An Error has occurred.");
            registerResponse.setWasSuccessful(false);
            registerResponse.setUuid(PlayerInfo.getUUID());
            new RegisterTaskResponse(registerResponse, PlayerInfo.getPlayer()).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
    }
}
