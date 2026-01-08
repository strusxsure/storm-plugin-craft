package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DuelListener implements Listener {

    private final ThunderPvPractice plugin;
    private final DuelManager duelManager;

    public DuelListener(ThunderPvPractice plugin, DuelManager duelManager) {
        this.plugin = plugin;
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player loser = event.getEntity();
        Duel duel = duelManager.getDuel(loser);
        if (duel != null) {
            Player winner = Bukkit.getPlayer(duel.getPlayer1().equals(loser.getUniqueId()) ? duel.getPlayer2() : duel.getPlayer1());
            duelManager.endDuel(duel, winner, loser);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player loser = event.getPlayer();
        Duel duel = duelManager.getDuel(loser);
        if (duel != null) {
            Player winner = Bukkit.getPlayer(duel.getPlayer1().equals(loser.getUniqueId()) ? duel.getPlayer2() : duel.getPlayer1());
            duelManager.endDuel(duel, winner, loser);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Duel duel = duelManager.getDuel(player);
        if (duel != null && duel.isFighting(player)) {
            if (!isInsideArena(player, duel.getArena())) {
                // For simplicity, we'll just teleport them back to their spawn.
                // A better implementation might push them back.
                if (player.getUniqueId().equals(duel.getPlayer1())) {
                    player.teleport(duel.getArena().getSpawn1());
                } else {
                    player.teleport(duel.getArena().getSpawn2());
                }
            }
        }
    }

    private boolean isInsideArena(Player player, com.thunderpvp.thunderpvppractice.arena.Arena arena) {
        org.bukkit.Location loc = player.getLocation();
        org.bukkit.Location c1 = arena.getCorner1();
        org.bukkit.Location c2 = arena.getCorner2();

        double minX = Math.min(c1.getX(), c2.getX());
        double maxX = Math.max(c1.getX(), c2.getX());
        double minY = Math.min(c1.getY(), c2.getY());
        double maxY = Math.max(c1.getY(), c2.getY());
        double minZ = Math.min(c1.getZ(), c2.getZ());
        double maxZ = Math.max(c1.getZ(), c2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
               loc.getY() >= minY && loc.getY() <= maxY &&
               loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}
