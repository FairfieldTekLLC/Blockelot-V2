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

    @EventHandler
    public void onPunchTree(BlockBreakEvent e) {
        int radius = 3;
        int height = 32;
        if (e.getBlock().getType().equals(Material.OAK_LOG)
                || e.getBlock().getType().equals(Material.BIRCH_LOG)
                || e.getBlock().getType().equals(Material.ACACIA_LOG)
                || e.getBlock().getType().equals(Material.SPRUCE_LOG)
                || e.getBlock().getType().equals(Material.JUNGLE_LOG)
                || e.getBlock().getType().equals(Material.DARK_OAK_LOG)
                || e.getBlock().getType().equals(Material.STRIPPED_OAK_LOG)
                || e.getBlock().getType().equals(Material.STRIPPED_BIRCH_LOG)
                || e.getBlock().getType().equals(Material.STRIPPED_ACACIA_LOG)
                || e.getBlock().getType().equals(Material.STRIPPED_SPRUCE_LOG)
                || e.getBlock().getType().equals(Material.STRIPPED_JUNGLE_LOG)
                || e.getBlock().getType().equals(Material.STRIPPED_DARK_OAK_LOG)
                || e.getBlock().getType().equals(Material.OAK_WOOD)
                || e.getBlock().getType().equals(Material.BIRCH_WOOD)
                || e.getBlock().getType().equals(Material.ACACIA_WOOD)
                || e.getBlock().getType().equals(Material.SPRUCE_WOOD)
                || e.getBlock().getType().equals(Material.JUNGLE_WOOD)
                || e.getBlock().getType().equals(Material.DARK_OAK_WOOD)
                || e.getBlock().getType().equals(Material.STRIPPED_OAK_WOOD)
                || e.getBlock().getType().equals(Material.STRIPPED_BIRCH_WOOD)
                || e.getBlock().getType().equals(Material.STRIPPED_ACACIA_WOOD)
                || e.getBlock().getType().equals(Material.STRIPPED_SPRUCE_WOOD)
                || e.getBlock().getType().equals(Material.STRIPPED_JUNGLE_WOOD)
                || e.getBlock().getType().equals(Material.STRIPPED_DARK_OAK_WOOD)) {
            Player p = e.getPlayer();
            ItemStack droppedItem = new ItemStack(e.getBlock().getType());
            if (p.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE) || p.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) || p.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) || p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) || p.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_AXE)) {

                Location broke = e.getBlock().getLocation();
                for (int j = 0; j < height; ++j) {

                    int Yt = (int) broke.getY() + j;
                    for (int i = -radius; i < radius + 1; ++i) {
                        int Xt = (int) broke.getX() + i;
                        for (int k = -radius; k < radius + 1; ++k) {
                            int Zt = (int) broke.getZ() + k;
                            Location l = new Location(p.getWorld(), (double) Xt, (double) Yt, (double) Zt);
                            if (l != broke && l.getBlock().getType() == droppedItem.getType()) {
                                l.getBlock().setType(Material.AIR);
                                l.getWorld().dropItem(l, droppedItem);
                            }
                        }
                    }
                }
            }
        }

    }
}
