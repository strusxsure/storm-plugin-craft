package com.thunderpvp.thunderpvppractice.gui;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import com.thunderpvp.thunderpvppractice.kit.Kit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitSelectionGUI extends GUI {

    private final List<Kit> kits;

    public KitSelectionGUI(ThunderPvPractice plugin, Player player) {
        super(plugin, player);
        this.kits = plugin.getKitManager().getKits();
    }

    @Override
    public String getTitle() {
        return "Select a Kit";
    }

    @Override
    public int getSize() {
        return 54; // 6 rows
    }

    @Override
    public void build() {
        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD); // You can customize this
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
            player.getInventory().setContents(kit.getInventory());
            player.getInventory().setArmorContents(kit.getArmor());
            player.addPotionEffects(kit.getPotionEffects());
            player.closeInventory();
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + "&aYou have received the " + kit.getName() + " kit."));
        }
    }
}
