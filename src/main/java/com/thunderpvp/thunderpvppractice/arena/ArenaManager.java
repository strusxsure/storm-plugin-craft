package com.thunderpvp.thunderpvppractice.arena;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArenaManager {

    private final ThunderPvPractice plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private FileConfiguration arenasConfig;
    private File arenasFile;

    public ArenaManager(ThunderPvPractice plugin) {
        this.plugin = plugin;
        setup();
        loadArenas();
    }

    private void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            try {
                arenasFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create arenas.yml!");
                e.printStackTrace();
            }
        }
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
    }

    public void saveArenas() {
        arenasConfig.set("arenas", null); // Clear existing arenas to prevent leftovers

        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getName();
            arenasConfig.set(path + ".spawn1", arena.getSpawn1());
            arenasConfig.set(path + ".spawn2", arena.getSpawn2());
            arenasConfig.set(path + ".corner1", arena.getCorner1());
            arenasConfig.set(path + ".corner2", arena.getCorner2());
        }
        try {
            arenasConfig.save(arenasFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save arenas.yml!");
            e.printStackTrace();
        }
    }

    public void loadArenas() {
        arenas.clear();
        ConfigurationSection arenasSection = arenasConfig.getConfigurationSection("arenas");
        if (arenasSection == null) {
            return;
        }
        for (String arenaName : arenasSection.getKeys(false)) {
            String path = "arenas." + arenaName;
            Location spawn1 = (Location) arenasConfig.get(path + ".spawn1");
            Location spawn2 = (Location) arenasConfig.get(path + ".spawn2");
            Location corner1 = (Location) arenasConfig.get(path + ".corner1");
            Location corner2 = (Location) arenasConfig.get(path + ".corner2");
            Arena arena = new Arena(arenaName, spawn1, spawn2);
            arena.setCorner1(corner1);
            arena.setCorner2(corner2);
            arenas.put(arenaName.toLowerCase(), arena);
        }
    }

    public boolean createArena(String name, Location corner1, Location corner2) {
        if (arenas.containsKey(name.toLowerCase())) {
            return false; // Arena already exists
        }
        Arena arena = new Arena(name);
        arena.setCorner1(corner1);
        arena.setCorner2(corner2);
        arenas.put(name.toLowerCase(), arena);
        saveArenas();
        return true;
    }

    public boolean deleteArena(String name) {
        if (!arenas.containsKey(name.toLowerCase())) {
            return false; // Arena does not exist
        }
        arenas.remove(name.toLowerCase());
        saveArenas();
        return true;
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public List<String> getArenaNames() {
        return arenas.values().stream().map(Arena::getName).collect(Collectors.toList());
    }

    public Arena getAvailableArena() {
        for (Arena arena : arenas.values()) {
            if (arena.getState() == ArenaState.AVAILABLE && arena.isReady()) {
                return arena;
            }
        }
        return null;
    }
}
