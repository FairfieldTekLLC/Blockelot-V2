package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import java.util.UUID;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.BlockCollection;
import com.Blockelot.worldeditor.container.BlockInfo;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CutTask
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
    UUID PlayerId;
    BlockCollection SchematicToPaste = new BlockCollection();
    private final BlockCollection Undo;

    public CutTask(int bx, int ex, int by, int ey, int bz, int ez, UUID playerId) {
        this.sbx = bx;
        this.sex = ex;
        this.sby = by;
        this.sey = ey;
        this.sbz = bz;
        this.sez = ez;
        this.cx = this.sbx;
        this.cy = this.sby;
        this.cz = this.sbz;
        this.PlayerId = playerId;
        Undo = PluginManager.GetPlayerInfo(playerId).NewUndo();
    }

    @Override
    public void run() {
        Player player = PluginManager.Plugin.getServer().getPlayer(this.PlayerId);
        try {

            PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());

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
                            this.SchematicToPaste.AddBlock(block, this.sbx, this.sby, this.sbz, null);
                            Undo.AddBlock(new BlockInfo(block, Undo), Undo);
                            block.setType(Material.AIR);
                        }
                        ++this.cz;
                        if (++counter > Configuration.MaxBlocksReadPerTick) {
                            try {
                                player.sendMessage("Copied " + this.SchematicToPaste.Size() + " blocks so far.. waiting..");
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
            int MinX = Integer.MAX_VALUE;
            int MinY = Integer.MAX_VALUE;
            int MinZ = Integer.MAX_VALUE;

            //Recenter the coords.
            for (BlockInfo info : SchematicToPaste.getBlocks()) {
                if (info.getX() < MinX) {
                    MinX = info.getX();
                }

                if (info.getY() < MinY) {
                    MinY = info.getY();
                }

                if (info.getZ() < MinZ) {
                    MinZ = info.getZ();
                }
            }

            for (BlockInfo info : SchematicToPaste.getBlocks()) {
                info.setX(info.getX() + (-1 * MinX));
                info.setY(info.getY() + (-1 * MinY));
                info.setZ(info.getZ() + (-1 * MinZ));
            }
            pi.ClipSchematic = this.SchematicToPaste;
            player.sendMessage("Blocks Copied (" + pi.ClipSchematic.Size() + " blocks copied.)");
            PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Cut");
            this.cancel();
        } catch (Exception e) {
            try {
                PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Cut");
            } catch (Exception loss) {

            }
            this.cancel();
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());

        }
        PluginManager.GetPlayerInfo(player.getUniqueId()).setIsProcessing(false, "Cut");
        this.cancel();
    }
}
