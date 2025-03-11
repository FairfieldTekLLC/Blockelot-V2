package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.http.LoginRequest;
import com.Blockelot.worldeditor.http.LoginResponse;
import com.Blockelot.worldeditor.http.RegisterResponse;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.JsonSyntaxException;


public class LoginTaskRequest
        extends HttpRequestor {

    private final PlayerInfo PlayerInfo;
    private final String Pass;

    public LoginTaskRequest(PlayerInfo pi, String pass) {
        PlayerInfo = pi;
        Pass = pass;
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "Login");
            Gson gson = new Gson();
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUuid(PlayerInfo.getUUID());
            loginRequest.setAuth(Pass);
            loginRequest.setWid(PluginManager.getWorldId());
            String body = gson.toJson(loginRequest);
            String page = RequestHttp(Configuration.BaseUri + "Login", body);
            LoginResponse response = gson.fromJson(page, LoginResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());
            response.setUuid(PlayerInfo.getUUID());

            new LoginTaskResponse(response).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);

        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {
            PlayerInfo.setIsProcessing(false, "Login");
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setMessage("An Error has occurred.");
            registerResponse.setWasSuccessful(false);
            registerResponse.setUuid(PlayerInfo.getUUID());
            new RegisterTaskResponse(registerResponse, PlayerInfo.getPlayer()).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
    }
}
