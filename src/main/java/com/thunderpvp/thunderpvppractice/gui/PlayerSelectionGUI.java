package com.thunderpvp.thunderpvppractice.gui;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionGUI extends GUI {

    private final List<Player> players;

    public PlayerSelectionGUI(ThunderPvPractice plugin, Player player) {
        super(plugin, player);
        this.players = new ArrayList<>(Bukkit.getOnlinePlayers());
        this.players.remove(player); // Can't duel yourself
    }

    @Override
    public String getTitle() {
        return "Select a Player to Duel";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void build() {
        for (int i = 0; i < players.size(); i++) {
            Player target = players.get(i);
            ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(target.getName());
            meta.setDisplayName(ThunderPvPractice.color("&a" + target.getName()));
            item.setItemMeta(meta);
            setItem(i, item);
        }
    }

    @Override
    public void onClick(int slot, ItemStack item) {
        if (slot >= 0 && slot < players.size()) {
            Player target = players.get(slot);
            new DuelKitSelectionGUI(plugin, player, target).open();
        }
    }
}
