package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.arena.Arena;
import com.thunderpvp.thunderpvppractice.kit.Kit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Duel {

    private final UUID player1;
    private final UUID player2;
    private final Kit kit;
    private final Arena arena;
    private DuelState state;

    public Duel(Player player1, Player player2, Kit kit, Arena arena) {
        this.player1 = player1.getUniqueId();
        this.player2 = player2.getUniqueId();
        this.kit = kit;
        this.arena = arena;
        this.state = DuelState.STARTING;
    }

    public UUID getPlayer1() {
        return player1;
    }

    public UUID getPlayer2() {
        return player2;
    }

    public Kit getKit() {
        return kit;
    }

    public Arena getArena() {
        return arena;
    }

    public DuelState getState() {
        return state;
    }

    public void setState(DuelState state) {
        this.state = state;
    }

    public boolean isFighting(Player player) {
        return state == DuelState.FIGHTING && (player.getUniqueId().equals(player1) || player.getUniqueId().equals(player2));
    }
}
