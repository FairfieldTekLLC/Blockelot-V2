package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import java.util.ListIterator;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.BlockInfo;
import com.Blockelot.worldeditor.container.PaletteEntry;
import com.Blockelot.worldeditor.http.SchematicDataRequest;
import com.Blockelot.worldeditor.http.SchematicDataResponse;
import com.Blockelot.worldeditor.container.BlockCollection;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.JsonSyntaxException;
import org.bukkit.ChatColor;

public class SaveClipboardTaskRequest
        extends HttpRequestor {

    private final String Filename;
    private PlayerInfo PlayerInfo;
    private boolean FirstPass = true;
    private BlockCollection WorkArea;
    private int TotalNumberOfBlocks = 0;
    int schematicId = -1;

    public SaveClipboardTaskRequest(PlayerInfo pi, String filename) {
        PlayerInfo = pi;
        WorkArea = pi.ClipSchematic.Clone();
        this.Filename = filename;
        TotalNumberOfBlocks = pi.ClipSchematic.Size();

    }

    @Override
    public void run() {
        try {

            SchematicDataResponse response = new SchematicDataResponse();
            while (WorkArea.Size() > 0) {
                ListIterator<BlockInfo> iter = WorkArea.getBlocks().listIterator();
                int blockCounter = 0;
                String blocks = "";
                while (iter.hasNext()) {
                    BlockInfo itm = iter.next();
                    blocks = blocks.concat(itm.toXferString());
                    iter.remove();
                    if (++blockCounter < Configuration.MaxBlocksUploadPerCall) {
                        continue;
                    }
                    break;
                }

                PlayerInfo.getPlayer().sendMessage(ChatColor.YELLOW + "Save in progress, " + WorkArea.Size() + " left to send...");

                SchematicDataRequest schematicDataRequest = new SchematicDataRequest();

                if (FirstPass) {
                    schematicDataRequest.setBlockTypePalette(WorkArea.GetBlockTypePalette());
                    schematicDataRequest.setBlockDataPalette(WorkArea.GetBlockDataPalette());
                    schematicDataRequest.setBlockInvePalette(WorkArea.GetBlockInventoryPalette());
                    FirstPass = false;
                } else {
                    PaletteEntry[] e = new PaletteEntry[1];
                    e[0] = new PaletteEntry();
                    e[0].setId(0);
                    e[0].setValue("");
                    schematicDataRequest.setBlockDataPalette(e);
                    schematicDataRequest.setBlockTypePalette(e);
                    schematicDataRequest.setBlockInvePalette(e);
                }
                Gson gson = new Gson();

                schematicDataRequest.setAuth(PlayerInfo.getLastAuth());
                schematicDataRequest.setCurrentDirectory(PlayerInfo.getCurrentPath());
                schematicDataRequest.setUuid(PlayerInfo.getUUID());
                schematicDataRequest.setFileName(this.Filename);
                schematicDataRequest.setBlocks(blocks);
                schematicDataRequest.setSchematicId(schematicId);
                schematicDataRequest.setTotalNumberOfBlocks(TotalNumberOfBlocks);
                String body = gson.toJson(schematicDataRequest);
                response = gson.fromJson(RequestHttp(Configuration.BaseUri + "Save", body), SchematicDataResponse.class);
                PlayerInfo.setLastAuth(response.getAuth());
                schematicId = response.getSchematicId();
                if (WorkArea.Size() > 1) {
                    response.setMessage("Saving... " + WorkArea.Size() + " blocks remaining of " + TotalNumberOfBlocks);
                    return;
                }

                PlayerInfo.setIsProcessing(false, "SaveClipboard");
                PlayerInfo.getPlayer().sendMessage(ChatColor.GREEN + response.getMessage());
                PlayerInfo.getPlayer().sendMessage(ChatColor.YELLOW + "File saved.");
                this.cancel();
            }
            response.setFinal(true);
            this.cancel();

        } catch (JsonSyntaxException | IllegalStateException e) {
            ServerUtil.consoleLog("ERROR");
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
            ServerUtil.consoleLog(e);

        }
        this.cancel();
    }
}
