package com.thunderpvp.thunderpvppractice.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUI) {
            event.setCancelled(true);
            GUI gui = (GUI) holder;
            gui.onClick(event.getSlot(), event.getCurrentItem());
        }
    }
}
