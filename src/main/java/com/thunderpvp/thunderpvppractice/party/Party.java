package com.thunderpvp.thunderpvppractice.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    private UUID leader;
    private final List<UUID> members = new ArrayList<>();

    public Party(Player leader) {
        this.leader = leader.getUniqueId();
        members.add(leader.getUniqueId());
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
    }

    public boolean isLeader(Player player) {
        return player.getUniqueId().equals(leader);
    }

    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public int getSize() {
        return members.size();
    }

    public void broadcast(String message) {
        for (UUID memberId : members) {
            Player member = org.bukkit.Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }
}
