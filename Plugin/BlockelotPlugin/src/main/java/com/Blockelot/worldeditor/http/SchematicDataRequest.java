package com.Blockelot.worldeditor.http;

import com.Blockelot.worldeditor.container.PaletteEntry;

public class SchematicDataRequest {
    private String Uuid;
    private String Auth;
    
    private int SchematicId;
    private String CurrentDirectory;
    private String Blocks;
    
    private String FileName;
    
    private int TotalNumberOfBlocks;
    
    public int getTotalNumberOfBlocks(){
        return TotalNumberOfBlocks;
    }
    
    public void setTotalNumberOfBlocks(int i){
        TotalNumberOfBlocks = i;
    }
    
    private PaletteEntry[] BlockDataPalette;
    private PaletteEntry[] BlockTypePalette;
    public PaletteEntry[] BlockInvePalette;
    public String getUuid() {
        return this.Uuid;
    }
    public void setUuid(String uuid) {
        this.Uuid = uuid;
    }
    public String getAuth() {
        return Auth;
    }
    public void setAuth(String auth) {
        Auth = auth;
    }
    public String getCurrentDirectory() {
        return this.CurrentDirectory;
    }
    public void setCurrentDirectory(String path) {
        this.CurrentDirectory = path;
    }

    public PaletteEntry[] getBlockInvePalette() {
        return BlockInvePalette;
    }

    public void setBlockInvePalette(PaletteEntry[] palette) {
        this.BlockInvePalette = palette;
    }

    public PaletteEntry[] getBlockDataPalette() {
        return BlockDataPalette;
    }

    public void setBlockDataPalette(PaletteEntry[] palette) {
        BlockDataPalette = palette;
    }

    public PaletteEntry[] getBlockTypePalette() {
        return BlockTypePalette;
    }

    public void setBlockTypePalette(PaletteEntry[] palette) {
        BlockTypePalette = palette;
    }

    public int getSchematicId() {
        return this.SchematicId;
    }

    public void setSchematicId(int id) {
        this.SchematicId = id;
    }

    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String name) {
        this.FileName = name;
    }

    public String getBlocks() {
        return this.Blocks;
    }

    public void setBlocks(String blocks) {
        this.Blocks = blocks;
    }
}
