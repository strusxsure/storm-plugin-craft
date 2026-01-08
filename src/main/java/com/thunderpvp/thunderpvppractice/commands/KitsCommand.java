package com.thunderpvp.thunderpvppractice.commands;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import com.thunderpvp.thunderpvppractice.gui.KitSelectionGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitsCommand implements CommandExecutor {

    private final ThunderPvPractice plugin;

    public KitsCommand(ThunderPvPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_only_command")));
            return true;
        }

        Player player = (Player) sender;
        new KitSelectionGUI(plugin, player).open();
        return true;
    }
}
