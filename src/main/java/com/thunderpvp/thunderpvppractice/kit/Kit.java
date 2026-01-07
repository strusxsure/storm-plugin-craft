package com.thunderpvp.thunderpvppractice.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class Kit {

    private String name;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private Collection<PotionEffect> potionEffects;

    public Kit(String name, ItemStack[] inventory, ItemStack[] armor, Collection<PotionEffect> potionEffects) {
        this.name = name;
        this.inventory = inventory;
        this.armor = armor;
        this.potionEffects = potionEffects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }
}
