package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.arena.Arena;
import com.thunderpvp.thunderpvppractice.kit.Kit;
import org.bukkit.entity.Player;

import java.util.UUID;

import java.util.List;

public class Duel {

    private final List<UUID> team1;
    private final List<UUID> team2;
    private final Kit kit;
    private final Arena arena;
    private DuelState state;

    public Duel(List<UUID> team1, List<UUID> team2, Kit kit, Arena arena) {
        this.team1 = team1;
        this.team2 = team2;
        this.kit = kit;
        this.arena = arena;
        this.state = DuelState.STARTING;
    }

    public List<UUID> getTeam1() {
        return team1;
    }

    public List<UUID> getTeam2() {
        return team2;
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
        return state == DuelState.FIGHTING && (team1.contains(player.getUniqueId()) || team2.contains(player.getUniqueId()));
    }
}
