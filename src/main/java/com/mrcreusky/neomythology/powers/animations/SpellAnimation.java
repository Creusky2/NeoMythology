package com.mrcreusky.neomythology.powers.animations;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;

public class SpellAnimation {
    private final String animationType;

    public SpellAnimation(String animationType) {
        this.animationType = animationType;
    }

    public String getAnimationType() {
        return animationType;
    }

    // Méthode pour jouer l'animation
    public void play(ServerLevel world, ServerPlayer caster, LivingEntity target) {
        switch (animationType) {
            case "light_beam":
                // Exemple d'animation de rayon lumineux
                playLightBeamAnimation(world, caster, target);
                break;
            case "explosion":
                // Exemple d'animation d'explosion
                world.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY(), target.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                break;
            case "frost":
                // Exemple d'animation de givre
                world.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(), target.getZ(), 20, 1.0, 1.0, 1.0, 0.2);
                break;
            default:
                // Aucune animation ou animation inconnue
                break;
        }
    }

    private void playLightBeamAnimation(ServerLevel world, ServerPlayer caster, LivingEntity target) {
        // Générer un rayon lumineux entre le lanceur et la cible
        Vec3 startVec = caster.getEyePosition(1.0F);
        Vec3 endVec = target.position();
        Vec3 direction = endVec.subtract(startVec).normalize();
        double distance = startVec.distanceTo(endVec);
        for (int i = 0; i < distance; i++) {
            Vec3 particlePos = startVec.add(direction.scale(i));
            world.sendParticles(ParticleTypes.END_ROD, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
        }
    }
}
