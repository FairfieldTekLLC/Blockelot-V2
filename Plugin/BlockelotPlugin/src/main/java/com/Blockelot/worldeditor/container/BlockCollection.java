package com.Blockelot.worldeditor.container;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.Blockelot.worldeditor.http.SchematicDataDownloadResponse;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;

/**
 *
 * @author geev
 */
public class BlockCollection {

    private ArrayList<BlockInfo> Blocks = new ArrayList<>();

    public ArrayList<BlockInfo> getBlocks() {
        return this.Blocks;
    }

    public static ArrayList<BlockInfo> BlocksGetForY(ArrayList<BlockInfo> toSort, int y) {
        ArrayList<BlockInfo> Result = new ArrayList<>();
        toSort.stream().filter(pe -> (pe.getY() == y)).forEachOrdered(pe -> {
            Result.add(pe);
        });
        return Result;
    }

    public static ArrayList<BlockInfo> SortYAscending(ArrayList<BlockInfo> toSort) {
        ArrayList<BlockInfo> result = new ArrayList<>();

        int minY = 1000000000;
        int maxY = 0;

        for (BlockInfo pe : toSort) {
            if (pe.getY() <= minY) {
                minY = pe.getY();
            }
            if (pe.getY() >= maxY) {
                maxY = pe.getY();
            }
        }

        for (int i = minY; i <= maxY; i++) {
            ArrayList<BlockInfo> d = BlocksGetForY(toSort, i);
            result.addAll(d);
        }

        return result;
    }

    public ArrayList<BlockInfo> getBlocksOrderYAscending() {
        return SortYAscending(Blocks);
    }

    public void setBlocks(ArrayList<BlockInfo> blocks) {
        this.Blocks = blocks;
    }

    public BlockInfo AddBlock(Block sourceBlock, int offsetX, int offsetY, int offsetZ, BlockCollection undo) throws Exception {

        if (Blocks.size() > Configuration.MaxClipboardSize) {
            throw new Exception("Schematic size exceeds server max.");
        }

        if (undo != null) {
            undo.AddBlock(sourceBlock, offsetX, offsetY, offsetZ, null);
        }

        Chunk chunk = sourceBlock.getChunk();
        World world = chunk.getWorld();
        if (!world.isChunkLoaded(chunk = world.getChunkAt(sourceBlock))) {
            world.loadChunk(chunk);
        }
        BlockInfo def = new BlockInfo(sourceBlock, this);

        def.setX(sourceBlock.getX() + offsetX);
        def.setY(sourceBlock.getY() + offsetY);
        def.setZ(sourceBlock.getZ() + offsetZ);

//        If it is the top of a door, we don't want to collect it.
//        Since it will be generated auto when we create the bottom of the
//        door.
        if (sourceBlock.getBlockData() instanceof Door door) {
            if (door.getHalf() == Half.TOP) {
                return def;
            }
        }

        Blocks.add(def);
        return def;
    }

    public BlockInfo AddBlock(BlockInfo blockDef, BlockCollection undo) throws Exception {

        if (Blocks.size() > Configuration.MaxClipboardSize) {
            throw new Exception("Schematic size exceeds server max.");
        }
        if (undo != null) {
            undo.AddBlock(blockDef, null);
        }
        BlockInfo clone;
        try {

            clone = blockDef.Clone(this);

            //If it is the top of a door, we don't want to collect it.
            //Since it will be generated auto when we create the bottom of the
            //door.
            if (clone.getBlockData() instanceof Door door) {
                if (door.getHalf() == Half.TOP) {
                    return clone;
                }
            }

            this.Blocks.add(clone);
            return clone;

        } catch (Exception ex) {
            Logger.getLogger(BlockCollection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private ArrayList<PaletteEntry> BlockTypePalette = new ArrayList<>();

    public int AddBlockTypeToPalette(Material mat) {
        for (PaletteEntry ent : BlockTypePalette) {
            if (ent.getValue().equals(mat.name().trim())) {
                return ent.getId();
            }
        }
        int idx = getMaxPalletId(BlockTypePalette) + 1;
        BlockTypePalette.add(new PaletteEntry(idx, mat.name().trim()));
        return idx;
    }

    public int AddBlockTypeToPalette(Block sourceBlock) {
        return AddBlockTypeToPalette(sourceBlock.getType());
    }

    public PaletteEntry[] GetBlockTypePalette() {
        PaletteEntry[] blockTypePalette = new PaletteEntry[BlockTypePalette.size()];
        BlockTypePalette.toArray(blockTypePalette);
        return blockTypePalette;
    }

    public Material GetBlockTypePaletteEntry(int id) {
        for (PaletteEntry ent : BlockTypePalette) {
            if (ent.getId() == id) {
                return Material.getMaterial(ent.getValue());
            }
        }
        return Material.AIR;
    }

    private ArrayList<PaletteEntry> BlockDataPalette = new ArrayList<>();

    public PaletteEntry[] GetBlockDataPalette() {
        PaletteEntry[] blockDataPalette = new PaletteEntry[BlockDataPalette.size()];
        BlockDataPalette.toArray(blockDataPalette);
        return blockDataPalette;
    }

    public String getBlockDataPalette(int i) {
        for (PaletteEntry ent : BlockDataPalette) {
            if (ent.getId() == i) {
                return ent.getValue();
            }
        }
        return "";
    }

    public int addBlockDataPalette(String value) {
        if (value == null) {
            value = "";
        }
        for (PaletteEntry ent : BlockDataPalette) {
            if (ent.getValue().equals(value.trim())) {
                return ent.getId();
            }
        }
        int idx = getMaxPalletId(BlockDataPalette) + 1;
        BlockDataPalette.add(new PaletteEntry(idx, value));
        return idx;
    }

    private ArrayList<PaletteEntry> BlockInventoryPalette = new ArrayList<>();

    public PaletteEntry[] GetBlockInventoryPalette() {
        PaletteEntry[] blockInventoryPalette = new PaletteEntry[BlockInventoryPalette.size()];
        BlockInventoryPalette.toArray(blockInventoryPalette);
        return blockInventoryPalette;
    }

    public String getBlockInventoryPalette(int i) {
        for (PaletteEntry ent : BlockInventoryPalette) {
            if (ent.getId() == i) {
                return ent.getValue();
            }
        }
        return "";
    }

    public int addBlockInventoryPalette(String value) {
        if (value == null) {
            value = "";
        }
        for (PaletteEntry ent : BlockInventoryPalette) {
            if (ent.getValue().equals(value.trim())) {
                return ent.getId();
            }
        }
        int idx = getMaxPalletId(BlockInventoryPalette) + 1;
        BlockInventoryPalette.add(new PaletteEntry(idx, value));
        return idx;
    }

    private String Name;

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public HashMap<String, Integer> GetBlockMaterialCounts() {
        HashMap<String, Integer> map = new HashMap<>();
        Blocks.stream().map((def) -> GetBlockTypePaletteEntry(def.getBlockTypeIndex()).name()).forEachOrdered((matName) -> {
            if (!map.containsKey(matName)) {
                map.put(matName, 1);
            } else {
                int count = map.get(matName) + 1;
                map.remove(matName);
                map.put(matName, count);
            }
        });
        return map;
    }

    public void LoadResponse(SchematicDataDownloadResponse response) {

        Blocks.clear();
        BlockTypePalette = new ArrayList<>();
        BlockDataPalette = new ArrayList<>();
        BlockInventoryPalette = new ArrayList<>();

        Name = response.getFileName();

        for (PaletteEntry pe : response.getBlockTypePalette()) {
            BlockTypePalette.add(pe.Clone());
        }

        for (PaletteEntry pe : response.getBlockDataPalette()) {
            BlockDataPalette.add(pe.Clone());
        }

        for (PaletteEntry pe : response.getBlockInvePalette()) {
            BlockInventoryPalette.add(pe.Clone());
        }

    }

    public int Size() {
        return Blocks.size();
    }

    public void Clear() {
        Blocks.clear();
        BlockTypePalette.clear();
        BlockDataPalette.clear();
    }

    public boolean IsEmpty() {
        return Blocks.isEmpty();
    }

    private int getMaxPalletId(ArrayList<PaletteEntry> palette) {
        int start = 0;
        for (PaletteEntry ent : palette) {
            if (ent.getId() > start) {
                start = ent.getId();
            }
        }
        return start;
    }

    public BlockCollection Clone() {

        BlockCollection newSchematicDef = new BlockCollection();

        BlockTypePalette.forEach((matType) -> {
            newSchematicDef.BlockTypePalette.add(matType.Clone());
        });
        BlockDataPalette.forEach((Data) -> {
            newSchematicDef.BlockDataPalette.add(Data.Clone());
        });

        Blocks.forEach((BlockInfo def) -> {
            try {
                newSchematicDef.Blocks.add(def.Clone(newSchematicDef));
            } catch (Exception ex) {
                Logger.getLogger(BlockCollection.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Validate();
        newSchematicDef.Validate();

        return newSchematicDef;
    }

    public boolean Validate() {
        Blocks.forEach((BlockInfo def) -> {
            if (def.getBlockData() == null) {
                ServerUtil.consoleLog("------------------------------------->NULL BLOCk data");
            }
            if (def.getBlockMaterial() == null) {
                ServerUtil.consoleLog("------------------------------------->NULL BLOCk Material");
            }
        });

        return true;
    }

}
