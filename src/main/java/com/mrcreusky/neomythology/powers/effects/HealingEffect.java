package com.mrcreusky.neomythology.powers.effects;

import com.mrcreusky.neomythology.powers.animations.SpellAnimation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class HealingEffect extends SpellEffect {
    private final float healAmount;

    public HealingEffect(float healAmount, SpellAnimation animation) {
        super("Healing", animation);
        this.healAmount = healAmount;
    }

    public float getHealAmount() {
        return healAmount;
    }

    @Override
    public void applyEffect(ServerPlayer caster, LivingEntity target) {
        // Soigne la cible
        target.heal(healAmount);
        
        // Joue l'animation associée si le monde est un serveur
        if (caster.level() instanceof ServerLevel) {
            playAnimation((ServerLevel) caster.level(), caster, target);
        }
    }
}
