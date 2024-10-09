package com.mrcreusky.neomythology.powers;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public abstract class Spell {
    private final String name;
    private final int cooldown; // Cooldown in ticks
    private final float damage; // Dégâts du sort

    public Spell(String name, int cooldown, float damage) {
        this.name = name;
        this.cooldown = cooldown;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    public float getDamage() {
        return damage;
    }

    // Méthode abstraite qui sera implémentée par chaque sort
    public abstract void cast(ServerPlayer player, ServerLevel world, Vec3 target, CooldownManager cooldownManager);

    // Vérifie si le sort peut être lancé (cooldown)
    public boolean canCast(ServerPlayer player, CooldownManager cooldownManager) {
        return !cooldownManager.isOnCooldown(player, this);
    }
}
