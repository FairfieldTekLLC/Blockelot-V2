package com.Blockelot.worldeditor.container;

import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
    public IPoint SelectStart = null;
    //World location end position.
    public IPoint SelectEnd = null;
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
