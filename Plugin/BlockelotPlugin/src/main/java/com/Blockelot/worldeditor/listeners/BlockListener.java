/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.listeners;

import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {
        Player player = event.getPlayer();

        PlayerInfo pi = PluginManager.GetPlayerInfo(player.getUniqueId());

        if (pi.AutoPickup && player.hasPermission("Blockelot.Player.AutoPickup")) {
            for (Item item : event.getItems()) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), item.getItemStack());
                    player.sendMessage("Inventory is Full.");
                } else {
                    event.setCancelled(true);
                    player.getInventory().addItem(new ItemStack[]{item.getItemStack()});
                }
            }
        }

    }

    
}
