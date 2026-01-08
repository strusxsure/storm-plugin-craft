package com.thunderpvp.thunderpvppractice.arena;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionListener implements Listener {

    private final ThunderPvPractice plugin;
    public final Map<UUID, Location> pos1Selections = new HashMap<>();
    public final Map<UUID, Location> pos2Selections = new HashMap<>();

    public SelectionListener(ThunderPvPractice plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand == null || itemInHand.getType() != Material.WOOD_AXE) {
            return;
        }

        if (!player.hasPermission("thunderpvp.admin")) {
            return;
        }

        Action action = event.getAction();
        Location location = event.getClickedBlock().getLocation();

        if (action == Action.LEFT_CLICK_BLOCK) {
            pos1Selections.put(player.getUniqueId(), location);
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.selection_pos1_set")));
            event.setCancelled(true);
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            pos2Selections.put(player.getUniqueId(), location);
            player.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.selection_pos2_set")));
            event.setCancelled(true);
        }
    }
}
