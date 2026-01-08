package com.thunderpvp.thunderpvppractice.gui;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import com.thunderpvp.thunderpvppractice.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DuelKitSelectionGUI extends GUI {

    private final Player target;
    private final List<Kit> kits;

    public DuelKitSelectionGUI(ThunderPvPractice plugin, Player player, Player target) {
        super(plugin, player);
        this.target = target;
        this.kits = plugin.getKitManager().getKits();
    }

    @Override
    public String getTitle() {
        return "Select a Kit for your Duel";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void build() {
        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD); // Customize as needed
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ThunderPvPractice.color("&a" + kit.getName()));
            item.setItemMeta(meta);
            setItem(i, item);
        }
    }

    @Override
    public void onClick(int slot, ItemStack item) {
        if (slot >= 0 && slot < kits.size()) {
            Kit kit = kits.get(slot);
            plugin.getDuelManager().createDuelRequest(player, target, kit.getName());
            player.closeInventory();
        }
    }
}
