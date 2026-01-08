package com.thunderpvp.thunderpvppractice.party;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand implements CommandExecutor {

    private final ThunderPvPractice plugin;
    private final PartyManager partyManager;

    public PartyCommand(ThunderPvPractice plugin, PartyManager partyManager) {
        this.plugin = plugin;
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_only_command")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            openPartyGUI(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                partyManager.createParty(player);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "accept":
                partyManager.acceptInvite(player);
                break;
            case "leave":
                partyManager.leaveParty(player);
                break;
            case "manage":
                openPartyGUI(player);
                break;
            case "duel":
                handleDuel(player, args);
                break;
            default:
                sendUsage(player);
                break;
        }
        return true;
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.party_invite_usage")));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_not_found")));
            return;
        }
        partyManager.invitePlayer(player, target);
    }

    private void sendUsage(Player player) {
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.party_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.party_create_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.party_invite_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.party_accept_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.party_leave_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.party_manage_usage")));
        player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.party_duel_usage")));
    }

    private void handleDuel(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.party_duel_usage")));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_not_found")));
            return;
        }

        new com.thunderpvp.thunderpvppractice.gui.DuelKitSelectionGUI(plugin, player, target).open();
    }

    private void openPartyGUI(Player player) {
        Party party = partyManager.getParty(player);
        if (party == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.not_in_party")));
            return;
        }
        new com.thunderpvp.thunderpvppractice.gui.PartyGUI(plugin, player, party).open();
    }
}
