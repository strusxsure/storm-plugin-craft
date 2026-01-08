package com.thunderpvp.thunderpvppractice;

import com.thunderpvp.thunderpvppractice.arena.ArenaCommand;
import com.thunderpvp.thunderpvppractice.arena.ArenaManager;
import com.thunderpvp.thunderpvppractice.commands.CommandManager;
import com.thunderpvp.thunderpvppractice.kit.KitCommand;
import com.thunderpvp.thunderpvppractice.kit.KitManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ThunderPvPractice extends JavaPlugin {

    public static ThunderPvPractice plugin;
    private CommandManager commandManager;
    private KitManager kitManager;
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        plugin = this;
        commandManager = new CommandManager();
        kitManager = new KitManager(this);
        arenaManager = new ArenaManager(this);

        getCommand("thunderpvp").setExecutor(commandManager);
        commandManager.registerCommand("kit", new KitCommand(this, kitManager));
        commandManager.registerCommand("arena", new ArenaCommand(this, arenaManager));

        saveDefaultConfig();

        getLogger().info("ThunderPvPractice has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ThunderPvPractice has been disabled!");
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public static String color(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
}
