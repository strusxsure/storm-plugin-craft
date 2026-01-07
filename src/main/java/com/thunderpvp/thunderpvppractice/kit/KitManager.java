package com.thunderpvp.thunderpvppractice.kit;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitManager {

    private final ThunderPvPractice plugin;
    private final Map<String, Kit> kits = new HashMap<>();
    private FileConfiguration kitsConfig;
    private File kitsFile;

    public KitManager(ThunderPvPractice plugin) {
        this.plugin = plugin;
        setup();
        loadKits();
    }

    private void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        kitsFile = new File(plugin.getDataFolder(), "kits.yml");
        if (!kitsFile.exists()) {
            try {
                kitsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create kits.yml!");
                e.printStackTrace();
            }
        }
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public void saveKits() {
        kitsConfig.set("kits", null); // Clear existing kits to prevent leftovers
        for (Kit kit : kits.values()) {
            String path = "kits." + kit.getName();
            kitsConfig.set(path + ".inventory", kit.getInventory());
            kitsConfig.set(path + ".armor", kit.getArmor());
            kitsConfig.set(path + ".potionEffects", new ArrayList<>(kit.getPotionEffects()));
        }
        try {
            kitsConfig.save(kitsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save kits.yml!");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadKits() {
        kits.clear();
        if (kitsConfig.getConfigurationSection("kits") == null) {
            return;
        }
        for (String kitName : kitsConfig.getConfigurationSection("kits").getKeys(false)) {
            String path = "kits." + kitName;
            List<ItemStack> inventoryList = (List<ItemStack>) kitsConfig.getList(path + ".inventory");
            ItemStack[] inventory = inventoryList != null ? inventoryList.toArray(new ItemStack[0]) : new ItemStack[0];
            List<ItemStack> armorList = (List<ItemStack>) kitsConfig.getList(path + ".armor");
            ItemStack[] armor = armorList != null ? armorList.toArray(new ItemStack[0]) : new ItemStack[0];
            List<PotionEffect> potionEffects = (List<PotionEffect>) kitsConfig.getList(path + ".potionEffects");

            Kit kit = new Kit(kitName, inventory, armor, potionEffects != null ? potionEffects : new ArrayList<>());
            kits.put(kitName.toLowerCase(), kit);
        }
    }

    public boolean createKit(String name, Player player) {
        if (kits.containsKey(name.toLowerCase())) {
            return false; // Kit already exists
        }
        Kit kit = new Kit(name, player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getActivePotionEffects());
        kits.put(name.toLowerCase(), kit);
        saveKits();
        return true;
    }

    public boolean deleteKit(String name) {
        if (!kits.containsKey(name.toLowerCase())) {
            return false; // Kit doesn't exist
        }
        kits.remove(name.toLowerCase());
        saveKits();
        return true;
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public List<String> getKitNames() {
        return kits.values().stream().map(Kit::getName).collect(Collectors.toList());
    }
}
