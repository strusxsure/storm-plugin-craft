package com.thunderpvp.thunderpvppractice.party;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

    private final ThunderPvPractice plugin;
    private final Map<UUID, Party> parties = new HashMap<>();
    private final Map<UUID, UUID> invites = new HashMap<>(); // Player invited -> Party leader

    public PartyManager(ThunderPvPractice plugin) {
        this.plugin = plugin;
    }

    public void createParty(Player leader) {
        parties.put(leader.getUniqueId(), new Party(leader));
        leader.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.party_created")));
    }

    public void disbandParty(Party party) {
        party.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.party_disbanded")));
        for (UUID memberId : party.getMembers()) {
            parties.remove(memberId);
        }
    }

    public void invitePlayer(Player inviter, Player target) {
        Party party = getParty(inviter);
        if (party == null || !party.isLeader(inviter)) {
            inviter.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.not_party_leader")));
            return;
        }

        if (getParty(target) != null) {
            inviter.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_already_in_party")));
            return;
        }

        invites.put(target.getUniqueId(), inviter.getUniqueId());
        inviter.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.invite_sent").replace("{player}", target.getName())));
        target.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.invite_received").replace("{player}", inviter.getName())));
    }

    public void acceptInvite(Player player) {
        UUID leaderId = invites.remove(player.getUniqueId());
        if (leaderId == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.no_pending_invite")));
            return;
        }

        Party party = getParty(org.bukkit.Bukkit.getPlayer(leaderId));
        if (party == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.party_no_longer_exists")));
            return;
        }

        party.addMember(player);
        parties.put(player.getUniqueId(), party);
        party.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_joined_party").replace("{player}", player.getName())));
    }

    public void leaveParty(Player player) {
        Party party = getParty(player);
        if (party == null) {
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.not_in_party")));
            return;
        }

        party.removeMember(player);
        parties.remove(player.getUniqueId());
        party.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.player_left_party").replace("{player}", player.getName())));

        if (party.getSize() == 0 || party.isLeader(player)) {
            disbandParty(party);
        }
    }

    public Party getParty(Player player) {
        return parties.get(player.getUniqueId());
    }
}
