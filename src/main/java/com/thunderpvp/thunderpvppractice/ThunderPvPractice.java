package com.thunderpvp.thunderpvppractice;

import com.thunderpvp.thunderpvppractice.arena.ArenaCommand;
import com.thunderpvp.thunderpvppractice.arena.ArenaManager;
import com.thunderpvp.thunderpvppractice.arena.SelectionListener;
import com.thunderpvp.thunderpvppractice.commands.CommandManager;
import com.thunderpvp.thunderpvppractice.kit.KitCommand;
import com.thunderpvp.thunderpvppractice.kit.KitManager;
import com.thunderpvp.thunderpvppractice.duel.DuelCommand;
import com.thunderpvp.thunderpvppractice.duel.DuelListener;
import com.thunderpvp.thunderpvppractice.duel.DuelManager;
import com.thunderpvp.thunderpvppractice.duel.PlayerJoinListener;
import com.thunderpvp.thunderpvppractice.gui.InventoryListener;
import com.thunderpvp.thunderpvppractice.gui.KitSelectionGUI;
import com.thunderpvp.thunderpvppractice.party.PartyCommand;
import com.thunderpvp.thunderpvppractice.party.PartyManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ThunderPvPractice extends JavaPlugin {

    private CommandManager commandManager;
    private KitManager kitManager;
    private ArenaManager arenaManager;
    private DuelManager duelManager;
    private PartyManager partyManager;

    @Override
    public void onEnable() {
        commandManager = new CommandManager(this);
        kitManager = new KitManager(this);
        arenaManager = new ArenaManager(this);
        duelManager = new DuelManager(this);
        partyManager = new PartyManager(this);
        SelectionListener selectionListener = new SelectionListener(this);
        DuelListener duelListener = new DuelListener(this, duelManager);
        PlayerJoinListener playerJoinListener = new PlayerJoinListener(this, duelManager);

        getServer().getPluginManager().registerEvents(selectionListener, this);
        getServer().getPluginManager().registerEvents(duelListener, this);
        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        getCommand("thunderpvp").setExecutor(commandManager);
        commandManager.registerCommand("kit", new KitCommand(this, kitManager));
        commandManager.registerCommand("arena", new ArenaCommand(this, arenaManager, selectionListener));

        DuelCommand duelCommand = new DuelCommand(this, duelManager);
        getCommand("duel").setExecutor(duelCommand);
        getCommand("accept").setExecutor(duelCommand);
        getCommand("deny").setExecutor(duelCommand);
        getCommand("kits").setExecutor(new com.thunderpvp.thunderpvppractice.commands.KitsCommand(this));
        getCommand("party").setExecutor(new PartyCommand(this, partyManager));

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

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public static String color(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
}
