package com.thunderpvp.thunderpvppractice.kit;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private final ThunderPvPractice plugin;
    private final KitManager kitManager;

    public KitCommand(ThunderPvPractice plugin, KitManager kitManager) {
        this.plugin = plugin;
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_only_command")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "create":
                if (args.length != 3) {
                    player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_create_usage")));
                    return true;
                }
                String createName = args[2];
                if (kitManager.createKit(createName, player)) {
                    player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_created").replace("{kit_name}", createName)));
                } else {
                    player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_already_exists")));
                }
                break;
            case "delete":
                if (args.length != 3) {
                    player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_delete_usage")));
                    return true;
                }
                String deleteName = args[2];
                if (kitManager.deleteKit(deleteName)) {
                    player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_deleted").replace("{kit_name}", deleteName)));
                } else {
                    player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_not_found")));
                }
                break;
            case "list":
                player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_list").replace("{kit_list}", String.join(", ", kitManager.getKitNames()))));
                break;
            default:
                sendUsage(player);
                break;
        }
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.kit_create_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.kit_delete_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.kit_list_usage")));
    }
}
