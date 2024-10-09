package com.mrcreusky.neomythology.powers.effects;

import com.mrcreusky.neomythology.powers.animations.SpellAnimation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class DamageEffect extends SpellEffect {
    private float damageAmount;

    public DamageEffect(float damageAmount, SpellAnimation animation) {
        super("Damage", animation);
        this.damageAmount = damageAmount;
    }

    // Getter pour les dégâts
    public float getDamageAmount() {
        return damageAmount;
    }

    // Setter pour les dégâts
    public void setDamageAmount(float damageAmount) {
        this.damageAmount = damageAmount;
    }

    @Override
    public void applyEffect(ServerPlayer caster, LivingEntity target) {
        target.hurt(caster.damageSources().magic(), damageAmount);
        
        // Jouer l'animation associée
        if (caster.level() instanceof ServerLevel) {
            playAnimation((ServerLevel) caster.level(), caster, target);
        }
    }
}
