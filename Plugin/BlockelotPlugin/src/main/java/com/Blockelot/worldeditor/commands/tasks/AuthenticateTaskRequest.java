package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.http.AuthenticateRequest;
import com.Blockelot.worldeditor.http.AuthenticateResponse;
import com.Blockelot.worldeditor.http.RegisterResponse;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

/*
This class handles user authentication requests.


*/
public class AuthenticateTaskRequest
        extends HttpRequestor {

    PlayerInfo PlayerInfo;

    public AuthenticateTaskRequest(@NotNull PlayerInfo pi) {
        this.PlayerInfo = pi;
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "Authenticate");
            Gson gson = new Gson();
            AuthenticateRequest authenticateRequest = new AuthenticateRequest();
            authenticateRequest.setUuid(PlayerInfo.getUUID());
            authenticateRequest.SetWid(PluginManager.getWorldId());

            String hr = RequestHttp(Configuration.BaseUri + "Authenticate", gson.toJson(authenticateRequest));
            AuthenticateResponse response = gson.fromJson(hr, AuthenticateResponse.class);
            //This shouldn't be here, cause really it shouldn't be.
            PlayerInfo.setLastAuth(response.getAuth());

            response.setUuid(PlayerInfo.getUUID());

            new AuthenticateTaskResponse(response).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {
            ServerUtil.consoleLog(e);
            PlayerInfo.setIsProcessing(false, "Authenticate");
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setMessage("An Error has occurred.");
            registerResponse.setWasSuccessful(false);
            registerResponse.setUuid(PlayerInfo.getUUID());
            new RegisterTaskResponse(registerResponse, PlayerInfo.getPlayer()).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
    }
}
