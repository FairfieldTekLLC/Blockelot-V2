package com.Blockelot.Util;

import static com.Blockelot.Util.Base64Coder.decodeLines;
import org.bukkit.inventory.ItemStack;
import java.io.*;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.*;
import org.bukkit.event.inventory.InventoryType;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.Blockelot.worldeditor.container.BlockInfo;
import org.bukkit.entity.Player;

/**
 *
 * @author geev
 */
public class Inventory {

    //This function converts the players inventory to a Base64 string so it
    //can't be sent over the internet.  It retrieves all of there inventory.
    public static String[] playerInventoryToBase64(Player player) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = itemStackArrayToBase64(player.getInventory().getContents());
        String armor = itemStackArrayToBase64(player.getInventory().getArmorContents());
        String ender = itemStackArrayToBase64(player.getEnderChest().getContents());

        return new String[]{content, armor, ender};
    }

    //This utility function takes a specific inventory and returns it as a 
    //Base64 string.
    public static String toBase64(org.bukkit.inventory.Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(inventory.getSize());
                dataOutput.writeObject(inventory.getType());
                for (int i = 0; i < inventory.getSize(); i++) {
                    dataOutput.writeObject(inventory.getItem(i));
                }
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    //This utility takes a base64 string of inventory data and converts it back 
    //to an inventory object which can be assigned to a player.
    public static org.bukkit.inventory.Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodeLines(data));
            org.bukkit.inventory.Inventory inventory;
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                InventoryType type = (InventoryType) dataInput.readObject();
                inventory = Bukkit.getServer().createInventory(null, type);
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, (ItemStack) dataInput.readObject());
                }
            }
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    //This function takes a Base 64 itemstack string and converts it to an 
    //ITemStack array for assignment.
    public static ItemStack[] itemStackfromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            ArrayList<ItemStack> itms;
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                InventoryType type = (InventoryType) dataInput.readObject();
                org.bukkit.inventory.Inventory inventory = Bukkit.getServer().createInventory(null, type);
                // Read the serialized inventory
                itms = new ArrayList<>();
                for (int i = 0; i < inventory.getSize(); i++) {
                    itms.add((ItemStack) dataInput.readObject());
                }
            }
            return itms.toArray(new ItemStack[itms.size()]);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    //This function takes an item stack array in Base64 format and converts
    //it to an ItemStack array object.
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            ItemStack[] items;
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                items = new ItemStack[dataInput.readInt()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }
            }
            return items;
        } catch (ClassNotFoundException e) {
            Logger.getLogger(BlockInfo.class.getName()).log(Level.SEVERE, null, e);
            throw new IOException("Unable to decode class type.", e);
        }
    }

    //This function converts an itemStack array into base64
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(items.length);
                for (ItemStack item : items) {
                    dataOutput.writeObject(item);
                }
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            Logger.getLogger(BlockInfo.class.getName()).log(Level.SEVERE, null, e);
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

}
