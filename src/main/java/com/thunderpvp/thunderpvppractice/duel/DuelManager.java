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

    public DuelManager(ThunderPvPractice plugin) {
        this.plugin = plugin;
    }

    public void createDuelRequest(Player requester, Player target, String kitName) {
        com.thunderpvp.thunderpvppractice.party.Party party1 = plugin.getPartyManager().getParty(requester);
        com.thunderpvp.thunderpvppractice.party.Party party2 = plugin.getPartyManager().getParty(target);

        if (party1 == null) { // Handle 1v1
            party1 = new com.thunderpvp.thunderpvppractice.party.Party(requester);
        }
        if (party2 == null) { // Handle 1v1
            party2 = new com.thunderpvp.thunderpvppractice.party.Party(target);
        }

        com.thunderpvp.thunderpvppractice.kit.Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            requester.sendMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.kit_not_found")));
            return;
        }
        DuelRequest request = new DuelRequest(plugin, requester, target, kit);
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

        com.thunderpvp.thunderpvppractice.party.Party party1 = plugin.getPartyManager().getParty(requester);
        com.thunderpvp.thunderpvppractice.party.Party party2 = plugin.getPartyManager().getParty(target);

        if (party1 == null) {
            party1 = new com.thunderpvp.thunderpvppractice.party.Party(requester);
        }
        if (party2 == null) {
            party2 = new com.thunderpvp.thunderpvppractice.party.Party(target);
        }

        startDuel(party1, party2, request.getKit());
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

    public void startDuel(com.thunderpvp.thunderpvppractice.party.Party party1, com.thunderpvp.thunderpvppractice.party.Party party2, com.thunderpvp.thunderpvppractice.kit.Kit kit) {
        Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) {
            party1.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.no_arenas_available")));
            party2.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.no_arenas_available")));
            return;
        }
        arena.setState(com.thunderpvp.thunderpvppractice.arena.ArenaState.IN_USE);

        List<UUID> team1 = party1.getMembers();
        List<UUID> team2 = party2.getMembers();

        for (UUID playerId : team1) {
            Player player = Bukkit.getPlayer(playerId);
            savePlayerState(player);
            player.teleport(arena.getSpawn1());
            player.getInventory().setContents(kit.getInventory());
            player.getInventory().setArmorContents(kit.getArmor());
            player.addPotionEffects(kit.getPotionEffects());
        }

        for (UUID playerId : team2) {
            Player player = Bukkit.getPlayer(playerId);
            savePlayerState(player);
            player.teleport(arena.getSpawn2());
            player.getInventory().setContents(kit.getInventory());
            player.getInventory().setArmorContents(kit.getArmor());
            player.addPotionEffects(kit.getPotionEffects());
        }

        Duel duel = new Duel(team1, team2, kit, arena);
        for (UUID playerId : team1) activeDuels.put(playerId, duel);
        for (UUID playerId : team2) activeDuels.put(playerId, duel);

        new BukkitRunnable() {
            int countdown = 5;
            @Override
            public void run() {
                if (countdown > 0) {
                    party1.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_starting_in").replace("{seconds}", String.valueOf(countdown))));
                    party2.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_starting_in").replace("{seconds}", String.valueOf(countdown))));
                    countdown--;
                } else {
                    duel.setState(DuelState.FIGHTING);
                    party1.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_started")));
                    party2.broadcast(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_started")));
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void endDuel(Duel duel, List<UUID> winners, List<UUID> losers) {
        duel.setState(DuelState.ENDING);

        if (winners != null && !winners.isEmpty()) {
            // In a real scenario, you'd format this list nicely.
            String winnerNames = winners.stream().map(uuid -> Bukkit.getPlayer(uuid).getName()).collect(java.util.stream.Collectors.joining(", "));
            Bukkit.broadcastMessage(ThunderPvPractice.color(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.duel_winner").replace("{player}", winnerNames)));
        }

        for (UUID playerId : duel.getTeam1()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                restorePlayerState(player);
            }
            activeDuels.remove(playerId);
        }
        for (UUID playerId : duel.getTeam2()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                restorePlayerState(player);
            }
            activeDuels.remove(playerId);
        }

        duel.getArena().setState(com.thunderpvp.thunderpvppractice.arena.ArenaState.AVAILABLE);
    }

    public void savePlayerState(Player player) {
        PlayerState state = new PlayerState(player);
        org.bukkit.configuration.file.FileConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
        state.save(config);
        try {
            java.io.File file = new java.io.File(plugin.getDataFolder(), "playerstates/" + player.getUniqueId() + ".yml");
            file.getParentFile().mkdirs();
            config.save(file);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void restorePlayerState(Player player) {
        java.io.File file = new java.io.File(plugin.getDataFolder(), "playerstates/" + player.getUniqueId() + ".yml");
        if (file.exists()) {
            org.bukkit.configuration.file.FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
            PlayerState.restore(player, config);
            file.delete();
        }
    }

    public Duel getDuel(Player player) {
        return activeDuels.get(player.getUniqueId());
    }
}
