package com.thunderpvp.thunderpvppractice.arena;

import org.bukkit.Location;

public class Arena {

    private final String name;
    private Location spawn1;
    private Location spawn2;
    private Location corner1;
    private Location corner2;
    private ArenaState state;

    public Arena(String name) {
        this.name = name;
        this.state = ArenaState.AVAILABLE; // Arenas are available by default
    }

    public Arena(String name, Location spawn1, Location spawn2) {
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.state = ArenaState.AVAILABLE;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public void setSpawn1(Location spawn1) {
        this.spawn1 = spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public void setSpawn2(Location spawn2) {
        this.spawn2 = spawn2;
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public boolean isReady() {
        return spawn1 != null && spawn2 != null && corner1 != null && corner2 != null;
    }
}
