package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.ThunderPvPractice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ThunderPvPractice plugin;
    private final DuelManager duelManager;

    public PlayerJoinListener(ThunderPvPractice plugin, DuelManager duelManager) {
        this.plugin = plugin;
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        duelManager.restorePlayerState(player);
    }
}
