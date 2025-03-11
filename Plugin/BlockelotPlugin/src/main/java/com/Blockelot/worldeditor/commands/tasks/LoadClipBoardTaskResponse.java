package com.Blockelot.worldeditor.commands.tasks;

import java.util.ArrayList;
import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.http.SchematicDataDownloadResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.Blockelot.worldeditor.container.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author geev
 */
public class LoadClipBoardTaskResponse
        extends BukkitRunnable {

    public SchematicDataDownloadResponse Response;

    Thread Sender = null;
    private boolean MadeRequest = false;
    PlayerInfo pi;
    Player player;
    ArrayList<BlockInfo> blocks;

    public LoadClipBoardTaskResponse(SchematicDataDownloadResponse response) {
        this.Response = response;
        player = PluginManager.Plugin.getServer().getPlayer(UUID.fromString(this.Response.getUuid()));
        pi = PluginManager.GetPlayerInfo(player.getUniqueId());

    }

    int counter = 0;

    @Override
    public void run() {
        try {

            if (player == null) {
                this.cancel();
                return;
            }

            if (!this.Response.getWasSuccessful()) {
                player.sendMessage(ChatColor.YELLOW + "File not loaded.");
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Load Clipboard");
                player.sendMessage(ChatColor.RED + this.Response.getMessage());
                this.cancel();
                return;
            }

            if (!MadeRequest) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(true, "Load Clipboard");
                pi.setLastAuth(this.Response.getAuth());
                pi.SelectEnd = null;
                pi.SelectStart = null;
                pi.getPlayer().sendMessage(ChatColor.YELLOW + "Loading Schematic...");
                pi.ClipSchematic.LoadResponse(Response);
                MadeRequest = true;
                blocks = new ArrayList<>();

                player.sendMessage("Expect Blocks:" + Response.getTotalNumberOfBlocks());
                player.sendMessage("Total Blocks: " + (Response.getBlocks().length / 7));

                Sender = new Thread(new SchematicLoaderThread(Response.getBlocks(), blocks, pi.ClipSchematic));
                Sender.start();
                return;
            }
            
            if (counter > 1) {
                pi.getPlayer().sendMessage(ChatColor.YELLOW + "Loading Schematic...");
                counter = -3;
            }

            if (Sender != null && Sender.isAlive()) {
                return;
            }

            pi.ClipSchematic.setBlocks(blocks);
            player.sendMessage(ChatColor.YELLOW + "Total Blocks Loaded: " + blocks.size());

            player.sendMessage(ChatColor.GREEN + this.Response.getMessage());

            PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Load Clipboard");
        } catch (IllegalStateException ex) {
            Logger.getLogger(BlockInfo.class.getName()).log(Level.WARNING, null, ex);
        }
        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Load Clipboard");
        this.cancel();
    }
}
