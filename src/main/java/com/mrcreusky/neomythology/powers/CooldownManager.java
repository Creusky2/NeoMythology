package com.mrcreusky.neomythology.powers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;

public class CooldownManager {
    // Map to store cooldowns for each player and each spell
    private final Map<String, Long> cooldowns = new HashMap<>();
    
    // Method to apply a cooldown for a spell
    public void applyCooldown(ServerPlayer player, Spell spell) {
        // Store the time when the spell was cast
        cooldowns.put(getPlayerSpellKey(player, spell), System.currentTimeMillis());
    }

    // Method to check if the spell is on cooldown
    public boolean isOnCooldown(ServerPlayer player, Spell spell) {
        String key = getPlayerSpellKey(player, spell);
        if (cooldowns.containsKey(key)) {
            long lastCastTime = cooldowns.get(key);
            long cooldownTime = spell.getCooldown() * 50L; // Convert ticks to milliseconds
            return (System.currentTimeMillis() - lastCastTime) < cooldownTime;
        }
        return false;
    }

    // Utility method to create a unique key for each player-spell pair
    private String getPlayerSpellKey(ServerPlayer player, Spell spell) {
        return player.getUUID().toString() + "_" + spell.getName();
    }
}
