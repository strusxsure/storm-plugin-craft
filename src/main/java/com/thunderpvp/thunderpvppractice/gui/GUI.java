package com.thunderpvp.thunderpvppractice.gui;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class GUI implements InventoryHolder {

    protected final ThunderPvPractice plugin;
    protected final Player player;
    protected Inventory inventory;

    public GUI(ThunderPvPractice plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public abstract String getTitle();
    public abstract int getSize();
    public abstract void build();

    public void open() {
        inventory = Bukkit.createInventory(this, getSize(), getTitle());
        build();
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public abstract void onClick(int slot, ItemStack item);
}
