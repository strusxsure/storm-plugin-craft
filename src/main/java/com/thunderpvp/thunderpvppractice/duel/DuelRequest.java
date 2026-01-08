package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.kit.Kit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelRequest {

    private final UUID requester;
    private final UUID target;
    private final Kit kit;
    private final long timestamp;
    private final com.thunderpvp.thunderpvppractice.ThunderPvPractice plugin;

    public DuelRequest(com.thunderpvp.thunderpvppractice.ThunderPvPractice plugin, Player requester, Player target, Kit kit) {
        this.plugin = plugin;
        this.requester = requester.getUniqueId();
        this.target = target.getUniqueId();
        this.kit = kit;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - timestamp > plugin.getConfig().getInt("duels.duel_request_timeout") * 1000;
    }
}
