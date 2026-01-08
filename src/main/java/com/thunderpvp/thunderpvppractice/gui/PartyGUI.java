package com.thunderpvp.thunderpvppractice.gui;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import com.thunderpvp.thunderpvppractice.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyGUI extends GUI {

    private final Party party;
    private final List<Player> members;

    public PartyGUI(ThunderPvPractice plugin, Player player, Party party) {
        super(plugin, player);
        this.party = party;
        this.members = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                members.add(member);
            }
        }
    }

    @Override
    public String getTitle() {
        return "Party Management";
    }

    @Override
    public int getSize() {
        return 27; // 3 rows
    }

    @Override
    public void build() {
        for (int i = 0; i < members.size(); i++) {
            Player member = members.get(i);
            ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(member.getName());
            String displayName = party.isLeader(member) ? "&b" + member.getName() + " (Leader)" : "&a" + member.getName();
            meta.setDisplayName(ThunderPvPractice.color(displayName));
            item.setItemMeta(meta);
            setItem(i, item);
        }

        // Add control items
        setItem(25, createControlItem(Material.REDSTONE_BLOCK, "&cLeave Party"));
        if (party.isLeader(player)) {
            setItem(26, createControlItem(Material.TNT, "&4Disband Party"));
        }
    }

    private ItemStack createControlItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ThunderPvPractice.color(name));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(int slot, ItemStack item) {
        if (item == null) return;

        if (slot < members.size()) {
            // Clicking on a player's head can open a sub-menu for kick/promote
            // For now, we'll keep it simple.
            Player clickedPlayer = members.get(slot);
            player.sendMessage(ThunderPvPractice.color("&eYou clicked on " + clickedPlayer.getName()));
        } else if (slot == 25) { // Leave Party
            plugin.getPartyManager().leaveParty(player);
            player.closeInventory();
        } else if (slot == 26 && party.isLeader(player)) { // Disband Party
            plugin.getPartyManager().disbandParty(party);
            player.closeInventory();
        }
    }
}
