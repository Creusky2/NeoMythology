package com.mrcreusky.neomythology.powers.effects;

import com.mrcreusky.neomythology.powers.animations.SpellAnimation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

public abstract class SpellEffect {
    private final String name;
    private final SpellAnimation animation; // Animation associée à l'effet

    public SpellEffect(String name, SpellAnimation animation) {
        this.name = name;
        this.animation = animation;
    }

    public String getName() {
        return name;
    }

    public SpellAnimation getAnimation() {
        return animation;
    }

    // Méthode abstraite que chaque effet doit implémenter
    public abstract void applyEffect(ServerPlayer caster, LivingEntity target);

    // Méthode pour jouer l'animation associée
    public void playAnimation(ServerLevel world, ServerPlayer caster, LivingEntity target) {
        if (animation != null) {
            animation.play(world, caster, target);
        }
    }
}
