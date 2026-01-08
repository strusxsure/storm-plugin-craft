package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {

    private final ThunderPvPractice plugin;
    private final DuelManager duelManager;

    public DuelCommand(ThunderPvPractice plugin, DuelManager duelManager) {
        this.plugin = plugin;
        this.duelManager = duelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_only_command")));
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("duel")) {
            handleDuel(player, args);
        } else if (label.equalsIgnoreCase("accept")) {
            handleAccept(player, args);
        } else if (label.equalsIgnoreCase("deny")) {
            handleDeny(player, args);
        }
        return true;
    }

    private void handleDuel(Player player, String[] args) {
        if (args.length > 0) {
            // Fallback to command-based duel for now
            if (args.length != 2) {
                player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_usage")));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_not_found")));
                return;
            }
            duelManager.createDuelRequest(player, target, args[1]);
        } else {
            new com.thunderpvp.thunderpvppractice.gui.PlayerSelectionGUI(plugin, player).open();
        }
    }

    private void handleAccept(Player player, String[] args) {
        // In a more complex system, you might specify who to accept from.
        // For now, we assume you accept the most recent request.
        duelManager.acceptDuelRequest(player);
    }

    private void handleDeny(Player player, String[] args) {
        duelManager.denyDuelRequest(player);
    }
}
