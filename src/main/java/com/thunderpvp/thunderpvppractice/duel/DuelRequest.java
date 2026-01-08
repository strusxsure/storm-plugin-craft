package com.thunderpvp.thunderpvppractice.duel;

import com.thunderpvp.thunderpvppractice.kit.Kit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelRequest {

    private final UUID requester;
    private final UUID target;
    private final Kit kit;
    private final long timestamp;

    public DuelRequest(Player requester, Player target, Kit kit) {
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
        // Requests expire after 60 seconds
        return System.currentTimeMillis() - timestamp > 60000;
    }
}
