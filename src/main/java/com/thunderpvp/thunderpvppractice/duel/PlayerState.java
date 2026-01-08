package com.thunderpvp.thunderpvppractice.duel;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerState {

    private final ItemStack[] inventory;
    private final ItemStack[] armor;
    private final Collection<PotionEffect> potionEffects;
    private final GameMode gameMode;
    private final Location location;
    private final double health;
    private final int hunger;
    private final float exp;

    public PlayerState(Player player) {
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.potionEffects = player.getActivePotionEffects();
        this.gameMode = player.getGameMode();
        this.location = player.getLocation();
        this.health = player.getHealth();
        this.hunger = player.getFoodLevel();
        this.exp = player.getExp();
    }

    public void save(org.bukkit.configuration.file.FileConfiguration config) {
        config.set("inventory", inventory);
        config.set("armor", armor);
        config.set("potionEffects", potionEffects);
        config.set("gameMode", gameMode.toString());
        config.set("location", location);
        config.set("health", health);
        config.set("hunger", hunger);
        config.set("exp", exp);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Player player, org.bukkit.configuration.file.FileConfiguration config) {
        player.getInventory().setContents((ItemStack[]) config.get("inventory"));
        player.getInventory().setArmorContents((ItemStack[]) config.get("armor"));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffects((Collection<PotionEffect>) config.get("potionEffects"));
        player.setGameMode(GameMode.valueOf(config.getString("gameMode")));
        player.teleport((Location) config.get("location"));
        player.setHealth(config.getDouble("health"));
        player.setFoodLevel(config.getInt("hunger"));
        player.setExp((float) config.getDouble("exp"));
    }
}
