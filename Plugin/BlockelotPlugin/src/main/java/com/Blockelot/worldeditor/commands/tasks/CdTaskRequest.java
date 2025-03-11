package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.Blockelot.worldeditor.http.CdRequest;
import com.Blockelot.worldeditor.http.CdResponse;
import com.Blockelot.worldeditor.http.RegisterResponse;
import com.google.gson.JsonSyntaxException;
import org.bukkit.entity.Player;

public class CdTaskRequest
        extends HttpRequestor {

    private final String Target;
    PlayerInfo PlayerInfo;
    Player Player;

    public CdTaskRequest(PlayerInfo pi, String target) {
        PlayerInfo = pi;
        this.Target = target;
        Player = pi.getPlayer();
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "Cd");
            Gson gson = new Gson();
            CdRequest cdRequest = new CdRequest();
            cdRequest.setAuth(PlayerInfo.getLastAuth());
            cdRequest.setCurrentDirectory(PlayerInfo.getCurrentPath());
            cdRequest.setUuid(PlayerInfo.getUUID());
            cdRequest.setTargetDirectory(this.Target);
            String body = gson.toJson(cdRequest);

            CdResponse response = gson.fromJson(RequestHttp(Configuration.BaseUri + "DirCd", body), CdResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());
            response.setUuid(PlayerInfo.getUUID());

            new CdTaskResponse(response)
                    .runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {

            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
            ServerUtil.consoleLog(e);

            PlayerInfo.setIsProcessing(false, "Cd");
            try {
                Player.sendMessage("An error has occurred.");
            } catch (Exception ex) {
                //
            }
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setMessage("An Error has occurred.");
            registerResponse.setWasSuccessful(false);
            registerResponse.setUuid(PlayerInfo.getUUID());
            new RegisterTaskResponse(registerResponse, Player).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
        PlayerInfo.setIsProcessing(false, "Cd");
        this.cancel();
    }
}
