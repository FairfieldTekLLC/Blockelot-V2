package com.Blockelot;

import java.io.Serializable;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Configuration implements Serializable {

    private static transient final long serialVersionUID = -1681012206529286330L;
    public String WorldId = "NEWSERVER";
    public int MaxClipboardSize = 1000000000;
    /*
    This is the URL the Java Plugin uses to talk to the backend with.
    If you are running your own custom backend, you will need to change it to
    the IP address or domain name the webserver is hosted on.
    
    The webserver does not need to be public unless you have minecraft servers
    connecting over the internet.
    */
    public String BaseUri = "http://Blockelot.com/api/worldeditor/v1/";
    //public String BaseUri = "http://192.168.211.52/api/worldeditor/v1/";
    //public String BaseUri = "http://localhost:31312/api/worldeditor/v1/";

    
    
    
    public String Permission_User = "Blockelot.WorldEditor.User";
    public String Permission_Clear = "Blockelot.WorldEditor.User.Clear";
    public String Permission_ClearHistory = "Blockelot.WorldEditor.User.ClearHistory";
    public String Permission_Size = "Blockelot.WorldEditor.User.Size";
    public String Permission_Print = "Blockelot.WorldEditor.User.Print";
    public String Permission_Select = "Blockelot.WorldEditor.User.Select";
    public String Permission_BlockelotBank = "Blockelot.Bank";
    public String Permission_Editor = "Blockelot.WorldEditor.Editor";
    public String Permission_Copy = "Blockelot.WorldEditor.Editor.Copy";
    public String Permission_Cut = "Blockelot.WorldEditor.Editor.Cut";
    public String Permission_Delete = "Blockelot.WorldEditor.Editor.Delete";
    public String Permission_Distr = "Blockelot.WorldEditor.Editor.Distr";
    public String Permission_Paste = "Blockelot.WorldEditor.Editor.Paste";
    public String Permission_StripMine = "Blockelot.WorldEditor.Editor.StripMine";
    public String Permission_Undo = "Blockelot.WorldEditor.Editor.Undo";
    public String Permission_FileSystem = "Blockelot.FileSystem.User";
    public String Permission_AutoPickup = "Blockelot.Player.AutoPickup";
    public Boolean IncludeInventoryWhenPasting = true;
    //Max number of blocks that will be modified per server tick.
    //you can adjust it up or down depending on how much load it 
    //puts on your server
    public int MaxBlocksWritePerTick = 5000;
    //Max number of blocks that will be read per tick.  Once again
    //adjust it up or down based on server power.
    public int MaxBlocksReadPerTick = 5000;
    //Max number of blocks that will be included in the HTTP call to upload it
    //to the web service.  I do not suggest editing it.
    public int MaxBlocksUploadPerCall = 20000;
    //Blocks that are non pastable using the paste command
    public String NonPastableBlocks = "";//IRON_BLOCK,GOLD_BLOCK,DIAMOND_BLOCK,BONE_BLOCK,COAL_BLOCK,DIAMOND_BLOCK,LAPIS_BLOCK,NETHERITE_BLOCK,QUART_BLOCK,SHULKER_BOX";
    public ArrayList<Material> NonPastableBlockArray = new ArrayList<>();

    public boolean SaveData() {
        FileConfiguration config = PluginManager.Plugin.getConfig();
        config.set("settings", "Blockelot.Com");
        config.set("settings.Non-Pastable.Blocks", NonPastableBlocks);

        config.set("settings.Description", "A tool to allow players to cut and paste across servers.");
        config.set("settings.HomePage", "Http://www.Blockelot.com");
        config.set("settings.Contact", "Vince@Fairfieldtek.com");
        config.set("settings.WorldId", WorldId);
        config.set("settings.config.MaxBlocksWritePerTick", MaxBlocksWritePerTick);
        config.set("settings.config.MaxBlocksReadPerTick", MaxBlocksReadPerTick);
        config.set("settings.config.MaxBlocksUploadPerCall", MaxBlocksUploadPerCall);
        config.set("settings.config.IncludeInventoryWhenPasting", IncludeInventoryWhenPasting);

        config.set("settings.config.maxclipboardsize", MaxClipboardSize);
        config.set("settings.config.BaseUri", BaseUri);
        config.set("settings.perms.user", Permission_User);
        config.set("settings.perms.clear", Permission_Clear);
        config.set("settings.perms.clearhistory", Permission_ClearHistory);
        config.set("settings.perms.size", Permission_Size);
        config.set("settings.perms.print", Permission_Print);
        config.set("settings.perms.select", Permission_Select);
        config.set("settings.perms.editor", Permission_Editor);
        config.set("settings.perms.copy", Permission_Copy);
        config.set("settings.perms.delete", Permission_Delete);
        config.set("settings.perms.distr", Permission_Distr);
        config.set("settings.perms.paste", Permission_Paste);
        config.set("settings.perms.stripmine", Permission_StripMine);
        config.set("settings.perms.undo", Permission_Undo);
        config.set("settings.perms.filesystem", Permission_FileSystem);
        config.set("settings.perms.bank",Permission_BlockelotBank);
        config.set("settings.perms.autopickup",Permission_AutoPickup);
        
        PluginManager.Plugin.saveConfig();
        return true;
    }

    public boolean LoadData() {
        FileConfiguration config = PluginManager.Plugin.getConfig();
        WorldId = config.getString("settings.WorldId");
        MaxClipboardSize = config.getInt("settings.config.maxclipboardsize");
        BaseUri = config.getString("settings.config.baseuri");
        Permission_User = config.getString("settings.perms.user");
        Permission_Clear = config.getString("settings.perms.clear");
        Permission_ClearHistory = config.getString("settings.perms.clearhistory");
        Permission_Size = config.getString("settings.perms.size");
        Permission_Print = config.getString("settings.perms.print");
        Permission_Select = config.getString("settings.perms.select");
        Permission_AutoPickup = config.getString("settings.perms.autopickup");

        Permission_Editor = config.getString("settings.perms.editor");
        Permission_Copy = config.getString("settings.perms.copy");
        Permission_Cut = config.getString("settings.perms.cut");
        Permission_Delete = config.getString("settings.perms.delete");
        Permission_Distr = config.getString("settings.perms.distr");
        Permission_Paste = config.getString("settings.perms.paste");
        Permission_StripMine = config.getString("settings.perms.stripmine");
        Permission_Undo = config.getString("settings.perms.undo");
        Permission_FileSystem = config.getString("settings.perms.filesystem");
        
        Permission_BlockelotBank=config.getString("settings.perms.bank");

        MaxBlocksReadPerTick = config.getInt("settings.config.MaxBlocksReadPerTick");
        MaxBlocksWritePerTick = config.getInt("settings.config.MaxBlocksWritePerTick");
        MaxBlocksWritePerTick = config.getInt("settings.config.MaxBlocksUploadPerCall");
        IncludeInventoryWhenPasting = config.getBoolean("IncludeInventoryWhenPasting");
        NonPastableBlocks = config.getString("settings.Non-Pastable.Blocks");

        String[] split = NonPastableBlocks.split(",");
        for (String s : split) {
            Material mat = Material.getMaterial(s);
            NonPastableBlockArray.add(mat);
        }

        return true;
    }

}
