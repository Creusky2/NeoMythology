package com.mrcreusky.neomythology.powers.effects;

import com.mrcreusky.neomythology.powers.animations.SpellAnimation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;

public class StatusEffect extends SpellEffect {
    private MobEffectInstance effect;

    public StatusEffect(MobEffectInstance effect, SpellAnimation animation) {
        super("Status", animation);
        this.effect = effect;
    }

    // Getter pour l'effet de statut
    public MobEffectInstance getEffect() {
        return effect;
    }

    // Setter pour l'effet de statut
    public void setEffect(MobEffectInstance effect) {
        this.effect = effect;
    }

    @Override
    public void applyEffect(ServerPlayer caster, LivingEntity target) {
        target.addEffect(new MobEffectInstance(effect));

        // Jouer l'animation associée
        if (caster.level() instanceof ServerLevel) {
            playAnimation((ServerLevel) caster.level(), caster, target);
        }
    }
}
