package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.GriefPreventionUtil;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.BlockCollection;
import com.Blockelot.worldeditor.container.BlockInfo;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DeleteTask
        extends BukkitRunnable {

    int sbx;
    int sex;
    int sby;
    int sey;
    int sbz;
    int sez;
    int cx = 0;
    int cy = 0;
    int cz = 0;
    
    private final BlockCollection Undo;
    Player player;

    public DeleteTask(int bx, int ex, int by, int ey, int bz, int ez, Player player) {
        this.sbx = bx;
        this.sex = ex;
        this.sby = by;
        this.sey = ey;
        this.sbz = bz;
        this.sez = ez;
        this.cx = this.sbx;
        this.cy = this.sby;
        this.cz = this.sbz;
        Undo = PluginManager.GetPlayerInfo(player.getUniqueId()).NewUndo();
        this.player = player;
    }

    @Override
    public void run() {
        
        try {

            

            if (player == null) {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Cut");
                this.cancel();
            }

            int counter = 0;
            World world = player.getWorld();

            while (this.cy <= this.sey) {
                while (this.cx <= this.sex) {
                    while (this.cz <= this.sez) {

                        Block block = world.getBlockAt(this.cx, this.cy, this.cz);
                        if (block.getType() != Material.BEDROCK) {
                            Undo.AddBlock(new BlockInfo(block, Undo), Undo);
                            block.setType(Material.AIR);
                        }
                        ++this.cz;
                        if (++counter > Configuration.MaxBlocksReadPerTick) {
                            try {
                                player.sendMessage("Deleting. waiting..");
                            } catch (Exception e) {
                                ServerUtil.consoleLog(e.getLocalizedMessage());
                                ServerUtil.consoleLog(e.getMessage());
                            }
                            return;
                        }

                    }
                    ++this.cx;
                    this.cz = this.sbz;
                }
                ++this.cy;
                this.cx = this.sbx;
            }

            player.sendMessage("Blocks Deleted.");
            PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Delete");
            this.cancel();
        } catch (Exception e) {
            try {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Delete");
            } catch (Exception loss) {

            }
            this.cancel();
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());

        }

        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Delete");
        this.cancel();
    }
}
