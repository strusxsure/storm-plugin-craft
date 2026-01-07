package com.thunderpvp.thunderpvppractice;

import com.thunderpvp.thunderpvppractice.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ThunderPvPractice extends JavaPlugin {

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        commandManager = new CommandManager();
        getCommand("thunderpvp").setExecutor(commandManager);

        saveDefaultConfig();

        getLogger().info("ThunderPvPractice has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ThunderPvPractice has been disabled!");
    }
}
