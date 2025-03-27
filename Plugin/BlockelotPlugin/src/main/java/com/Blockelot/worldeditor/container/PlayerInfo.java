package com.Blockelot.worldeditor.container;

import static com.Blockelot.Util.MiscUtil.getHollowCube;
import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PlayerInfo {

    public PlayerInfo(Player player) {
        Player = player;
        setUUID(player.getUniqueId().toString());
    }

    //Minecraft ID of the player
    private String UUID;

    //Player Object
    private Player Player;

    public Player getPlayer() {
        return Player;
    }

    public void setPlayer(Player player) {
        Player = player;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String uuid) {
        UUID = uuid;
    }

    

    //Last authorization key used.  Passed when you need to make a new call.
    private String LastAuth = "";
    //Current path location in virtual file system.
    private String CurrentPath = "";
    //World location start position
    private IPoint SelectStart = null;
    //World location end position.

    public IPoint getSelectStart() {
        return SelectStart;
    }

    public void setSelectStart(IPoint loc) {
        SelectStart = loc;
    }

    public IPoint getSelectEnd() {
        return SelectEnd;
    }

    public void setSelectEnd(IPoint loc) {
        SelectEnd = loc;
        TurnOnSelectionBox();
    }

    public void ClearSelectedArea() {
        SelectStart = null;
        SelectEnd = null;
        TurnOffSelectionBox();
    }

    Block Select1Block;
    Material Select1BlockMat;
    Block Select2Block;
    Material Select2BlockMat;
    Block Select3Block;
    Material Select3BlockMat;
    Block Select4Block;
    Material Select4BlockMat;
    Block Select5Block;
    Material Select5BlockMat;
    Block Select6Block;
    Material Select6BlockMat;
    Block Select7Block;
    Material Select7BlockMat;
    Block Select8Block;
    Material Select8BlockMat;
            
    public void TurnOffSelectionBox(){
        Select1Block.setType(Select1BlockMat);
        Select2Block.setType(Select2BlockMat);
        Select3Block.setType(Select3BlockMat);
        Select4Block.setType(Select4BlockMat);
        Select5Block.setType(Select5BlockMat);
        Select6Block.setType(Select6BlockMat);
        Select7Block.setType(Select7BlockMat);
        Select8Block.setType(Select8BlockMat);
        
    }
    
    public void TurnOnSelectionBox() {
        if (SelectStart == null || SelectEnd == null) {
            return;
        }

        int sbx;
        int sex;
        int sby;
        int sey;
        int sbz;
        int sez;

        if (getSelectStart().X > getSelectEnd().X) {
            sbx = getSelectEnd().X;
            sex = getSelectStart().X;
        } else {
            sex = getSelectEnd().X;
            sbx = getSelectStart().X;
        }
        if (getSelectStart().Y > getSelectEnd().Y) {
            sby = getSelectEnd().Y;
            sey = getSelectStart().Y;
        } else {
            sey = getSelectEnd().Y;
            sby = getSelectStart().Y;
        }
        if (getSelectStart().Z > getSelectEnd().Z) {
            sbz = getSelectEnd().Z;
            sez = getSelectStart().Z;
        } else {
            sez = getSelectEnd().Z;
            sbz = getSelectStart().Z;
        }

        Location loc = new Location(Player.getWorld(), sbx, sby, sbz);
        Select1Block = loc.getBlock();
        Select1BlockMat = Select1Block.getType();
        Select1Block.setType(Material.GLOWSTONE);

        loc = new Location(Player.getWorld(), sbx, sey, sez);
        Select2Block = loc.getBlock();
        Select2BlockMat = Select2Block.getType();
        Select2Block.setType(Material.GLOWSTONE);


        loc = new Location(Player.getWorld(), sbx, sby, sez);
        Select3Block = loc.getBlock();
        Select3BlockMat = Select3Block.getType();
        Select3Block.setType(Material.GLOWSTONE);


        loc = new Location(Player.getWorld(), sbx, sey, sbz);
        Select4Block = loc.getBlock();
        Select4BlockMat = Select4Block.getType();
        Select4Block.setType(Material.GLOWSTONE);

        
        loc = new Location(Player.getWorld(), sex, sby, sbz);
        Select5Block = loc.getBlock();
        Select5BlockMat = Select5Block.getType();
        Select5Block.setType(Material.GLOWSTONE);


        loc = new Location(Player.getWorld(), sex, sey, sez);
        Select6Block = loc.getBlock();
        Select6BlockMat = Select6Block.getType();
        Select6Block.setType(Material.GLOWSTONE);


        loc = new Location(Player.getWorld(), sex, sby, sez);
        Select7Block = loc.getBlock();
        Select7BlockMat = Select7Block.getType();
        Select7Block.setType(Material.GLOWSTONE);


        loc = new Location(Player.getWorld(), sex, sey, sbz);
        Select8Block = loc.getBlock();
        Select8BlockMat = Select8Block.getType();
        Select8Block.setType(Material.GLOWSTONE);

        

    }

    private IPoint SelectEnd = null;
    //Whether autopickup is enabled or not.
    public boolean AutoPickup = false;
    //Players current clip board
    public BlockCollection ClipSchematic = new BlockCollection();
    //Current undo collection
    private BlockCollection UndoSchematic = new BlockCollection();
    //Change stack for undo.
    private final Stack<BlockCollection> UndoHistory = new Stack<>();

    //Flying
    public boolean IsFlying = false;

    //Creates a new undo stack
    public BlockCollection NewUndo() {
        UndoHistory.push(UndoSchematic);
        UndoSchematic = new BlockCollection();
        return UndoSchematic;
    }

    //Gets the last undo collection from the stack
    public BlockCollection GetUndo() {
        BlockCollection current = UndoSchematic;
        if (!UndoHistory.empty()) {
            UndoSchematic = UndoHistory.pop();
        } else {
            UndoSchematic = new BlockCollection();
        }
        return current;
    }

    //Clears the undo stack.
    public void ClearHistory() {
        UndoSchematic = new BlockCollection();
        UndoHistory.empty();
    }

    //This is used for the two-factor authorization which is currently turned off.
    public String Token;

    //Flag used to cancel an undo or paste.  Currently no command exists
    //to trigger it.
    //Todo  Add command to trigger it.
    public boolean CancelLastAction = false;

    //Flag indicating the system is working on something.
    private boolean IsProcessing;

    public boolean getIsProcessing() {
        return this.IsProcessing;
    }

    public void setIsProcessing(boolean flag, String caller) {
        IsProcessing = flag;
        //ServerUtil.consoleLog(caller + " is setting Busy: " + flag);
    }

    //Gets the current path in the file system
    public String getCurrentPath() {
        return this.CurrentPath;
    }

    public void setCurrentPath(String path) {
        this.CurrentPath = path;
    }

    public String getLastAuth() {
        return this.LastAuth;
    }

    public void setLastAuth(String lastAuth) {
        this.LastAuth = lastAuth;
    }

    //sends a message to the player with surrounding -----------
    private String GenLineCenter(String msg) {
        int lineLength = 52;
        int msgLength = msg.length();
        int startPos = (lineLength / 2) - msgLength / 2;

        final String lineBeginFormat = ChatColor.GOLD + "#" + ChatColor.BLACK + "--------------------------------------------------";
        final String lineEndFormat = "--------------------------------------------------";

        String OutLine = lineBeginFormat.substring(0, startPos) + ChatColor.YELLOW + msg.trim() + ChatColor.BLACK + lineEndFormat;

        OutLine = OutLine.substring(0, 57) + ChatColor.GOLD + " #";
        return OutLine;
    }

    private String GenLineLeft(String msg) {
        String OutLine = ChatColor.GOLD + "#" + ChatColor.BLACK + "-" + ChatColor.YELLOW + msg + ChatColor.BLACK + "--------------------------------------------------------------";
        OutLine = OutLine.substring(0, 57) + ChatColor.GOLD + " #";
        return OutLine;

    }

    public void SendBankMessageHeader(ArrayList<String> msgs, boolean leftJustify) {
        SendBankMessageHeader(msgs, leftJustify, true);
    }

    public void SendBankMessageHeader(ArrayList<String> msgs, boolean leftJustify, boolean doBorder) {
        getPlayer().sendMessage(ChatColor.GOLD + "###################################################");
        getPlayer().sendMessage(ChatColor.GOLD + "#--Welcome to the First Minecraft Bank of Blockelot--#".toUpperCase());
        getPlayer().sendMessage(ChatColor.GOLD + "###################################################");
        for (String line : msgs) {

            if (doBorder) {
                if (!leftJustify) {
                    getPlayer().sendMessage(GenLineCenter(line));
                } else {
                    getPlayer().sendMessage(GenLineLeft(line));
                }
            } else {
                getPlayer().sendMessage(line);
            }
        }

        getPlayer().sendMessage(ChatColor.GOLD + "###################################################");
    }
}
