package com.Blockelot.worldeditor.commands.tasks;

import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.http.SchematicDataDownloadResponse;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author geev
 */
public class LoadClipboardTaskRequest
        extends HttpRequestor {

    private final PlayerInfo PlayerInfo;

    private final String Filename;

    private boolean MadeRequest = false;

    Thread Sender = null;

    public LoadClipboardTaskRequest(PlayerInfo playerInfo, String filename) {

        this.Filename = filename;
        this.PlayerInfo = playerInfo;

    }

    StringObject result = new StringObject();

    @Override
    public void run() {
        try {
            if (!MadeRequest) {
                Sender = new Thread(new ThreadAlive(PlayerInfo, Filename, result));
                Sender.start();
                MadeRequest = true;
            }
            PlayerInfo.getPlayer().sendMessage("Waiting for response....");
            if (Sender.isAlive()) {
                return;
            }
            PlayerInfo.getPlayer().sendMessage("Reading response....");
            Gson gson = new Gson();
            SchematicDataDownloadResponse response = gson.fromJson(result.getString(), SchematicDataDownloadResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());
            response.setUuid(PlayerInfo.getUUID());

            LoadClipBoardTaskResponse ct = new LoadClipBoardTaskResponse(response);
            ct.runTaskTimer((org.bukkit.plugin.Plugin) PluginManager.Plugin, 2, 15);

        } catch (JsonSyntaxException | IllegalArgumentException | IllegalStateException e) {
            PlayerInfo.setIsProcessing(false, "LoadClipboard");
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
            SchematicDataDownloadResponse resp = new SchematicDataDownloadResponse();
            resp.setFileName(this.Filename);
            resp.setWasSuccessful(false);
            resp.setUuid(PlayerInfo.getUUID());
            resp.setMessage("Unknown Error.");
            new LoadClipBoardTaskResponse(resp).runTask((org.bukkit.plugin.Plugin) PluginManager.Plugin);
        }
        cancel();
    }
}
