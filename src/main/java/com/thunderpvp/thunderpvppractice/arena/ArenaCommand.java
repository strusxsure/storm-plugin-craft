package com.thunderpvp.thunderpvppractice.arena;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    private final ThunderPvPractice plugin;
    private final ArenaManager arenaManager;
    private final SelectionListener selectionListener;

    public ArenaCommand(ThunderPvPractice plugin, ArenaManager arenaManager, SelectionListener selectionListener) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.selectionListener = selectionListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_only_command")));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("thunderpvp.admin")) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        if (args.length < 2) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "setspawn1":
                handleSetSpawn(player, args, 1);
                break;
            case "setspawn2":
                handleSetSpawn(player, args, 2);
                break;
            case "list":
                handleList(player);
                break;
            default:
                sendUsage(player);
                break;
        }
        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_create_usage")));
            return;
        }
        String arenaName = args[2];

        org.bukkit.Location pos1 = selectionListener.pos1Selections.get(player.getUniqueId());
        org.bukkit.Location pos2 = selectionListener.pos2Selections.get(player.getUniqueId());

        if (pos1 == null || pos2 == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.selection_not_set")));
            return;
        }

        if (arenaManager.createArena(arenaName, pos1, pos2)) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_created").replace("{arena_name}", arenaName)));
        } else {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_already_exists")));
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_delete_usage")));
            return;
        }
        String arenaName = args[2];
        if (arenaManager.deleteArena(arenaName)) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_deleted").replace("{arena_name}", arenaName)));
        } else {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_not_found")));
        }
    }

    private void handleSetSpawn(Player player, String[] args, int spawnNumber) {
        if (args.length != 3) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_setspawn_usage")));
            return;
        }
        String arenaName = args[2];
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_not_found")));
            return;
        }
        if (spawnNumber == 1) {
            arena.setSpawn1(player.getLocation());
        } else {
            arena.setSpawn2(player.getLocation());
        }
        arenaManager.saveArenas();
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_spawn_set").replace("{spawn_number}", String.valueOf(spawnNumber)).replace("{arena_name}", arenaName)));
    }

    private void handleList(Player player) {
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_list").replace("{arena_list}", String.join(", ", arenaManager.getArenaNames()))));
    }

    private void sendUsage(Player player) {
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.arena_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.arena_create_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.arena_delete_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.arena_setspawn_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.arena_list_usage")));
    }
}
