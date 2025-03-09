/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Blockelot.worldeditor.listeners;

import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

/**
 *
 * @author geev
 */
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