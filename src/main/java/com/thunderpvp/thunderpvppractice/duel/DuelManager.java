package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import com.thunderpvp.thunderpvppractice.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DuelManager {

    private final ThunderPvPractice plugin;
    private final Map<UUID, DuelRequest> duelRequests = new HashMap<>();
    private final Map<UUID, Duel> activeDuels = new HashMap<>();
    private final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> playerArmor = new HashMap<>();
    private final Map<UUID, Collection<PotionEffect>> playerEffects = new HashMap<>();
    private final Map<UUID, GameMode> playerGameModes = new HashMap<>();

    public DuelManager(ThunderPvPractice plugin) {
        this.plugin = plugin;
    }

    public void createDuelRequest(Player requester, Player target, String kitName) {
        com.thunderpvp.thunderpvppractice.kit.Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            requester.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_not_found")));
            return;
        }
        DuelRequest request = new DuelRequest(requester, target, kit);
        duelRequests.put(target.getUniqueId(), request);

        requester.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_request_sent").replace("{player}", target.getName())));
        target.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_request_received").replace("{player}", requester.getName())));
    }

    public void acceptDuelRequest(Player target) {
        DuelRequest request = duelRequests.get(target.getUniqueId());
        if (request == null || request.hasExpired()) {
            target.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.no_pending_request")));
            return;
        }

        Player requester = Bukkit.getPlayer(request.getRequester());
        if (requester == null) {
            target.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.requester_offline")));
            return;
        }

        duelRequests.remove(target.getUniqueId());
        startDuel(requester, target, request.getKit());
    }

    public void denyDuelRequest(Player target) {
        DuelRequest request = duelRequests.remove(target.getUniqueId());
        if (request != null) {
            Player requester = Bukkit.getPlayer(request.getRequester());
            if (requester != null) {
                requester.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_request_denied").replace("{player}", target.getName())));
            }
            target.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.you_denied_request")));
        }
    }

    public void startDuel(Player player1, Player player2, com.thunderpvp.thunderpvppractice.kit.Kit kit) {
        Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) {
            player1.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + "&cNo arenas are available right now."));
            player2.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + "&cNo arenas are available right now."));
            return;
        }

        savePlayerState(player1);
        savePlayerState(player2);

        player1.teleport(arena.getSpawn1());
        player2.teleport(arena.getSpawn2());

        player1.getInventory().setContents(kit.getInventory());
        player1.getInventory().setArmorContents(kit.getArmor());
        player1.addPotionEffects(kit.getPotionEffects());

        player2.getInventory().setContents(kit.getInventory());
        player2.getInventory().setArmorContents(kit.getArmor());
        player2.addPotionEffects(kit.getPotionEffects());

        Duel duel = new Duel(player1, player2, kit, arena);
        activeDuels.put(player1.getUniqueId(), duel);
        activeDuels.put(player2.getUniqueId(), duel);

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    player1.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_starting_in").replace("{seconds}", String.valueOf(countdown))));
                    player2.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_starting_in").replace("{seconds}", String.valueOf(countdown))));
                    countdown--;
                } else {
                    duel.setState(DuelState.FIGHTING);
                    player1.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_started")));
                    player2.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_started")));
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void endDuel(Duel duel, Player winner, Player loser) {
        duel.setState(DuelState.ENDING);

        if (winner != null) {
            Bukkit.broadcastMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_winner").replace("{player}", winner.getName())));
        }

        restorePlayerState(Bukkit.getPlayer(duel.getPlayer1()));
        restorePlayerState(Bukkit.getPlayer(duel.getPlayer2()));

        activeDuels.remove(duel.getPlayer1());
        activeDuels.remove(duel.getPlayer2());
    }

    public void savePlayerState(Player player) {
        playerInventories.put(player.getUniqueId(), player.getInventory().getContents());
        playerArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
        playerEffects.put(player.getUniqueId(), player.getActivePotionEffects());
        playerGameModes.put(player.getUniqueId(), player.getGameMode());
    }

    public void restorePlayerState(Player player) {
        player.getInventory().setContents(playerInventories.get(player.getUniqueId()));
        player.getInventory().setArmorContents(playerArmor.get(player.getUniqueId()));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffects(playerEffects.get(player.getUniqueId()));
        player.setGameMode(playerGameModes.get(player.getUniqueId()));

        playerInventories.remove(player.getUniqueId());
        playerArmor.remove(player.getUniqueId());
        playerEffects.remove(player.getUniqueId());
        playerGameModes.remove(player.getUniqueId());
    }

    public Duel getDuel(Player player) {
        return activeDuels.get(player.getUniqueId());
    }
}
