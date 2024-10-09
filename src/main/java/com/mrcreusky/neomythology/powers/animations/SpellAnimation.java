package com.mrcreusky.neomythology.powers.animations;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;

public class SpellAnimation {
    private final String animationType;
    private final SoundEvent soundEvent; // Son associé à l'animation
    private final float volume;
    private final float pitch;

    public SpellAnimation(String animationType, ResourceLocation soundResource, float volume, float pitch) {
        this.animationType = animationType;
        // Utilisation de BuiltInRegistries pour obtenir le SoundEvent
        this.soundEvent = BuiltInRegistries.SOUND_EVENT.getOptional(soundResource).orElse(null);
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getAnimationType() {
        return animationType;
    }

    // Méthode pour jouer l'animation et le son associé
    public void play(ServerLevel world, ServerPlayer caster, LivingEntity target) {
        // Jouer l'animation spécifique en fonction du type
        switch (animationType) {
            case "light_beam":
                playLightBeamAnimation(world, caster, target);
                break;
            case "explosion":
                world.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY(), target.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                break;
            case "frost":
                world.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(), target.getZ(), 20, 1.0, 1.0, 1.0, 0.2);
                break;
            default:
                // Aucune animation ou animation inconnue
                break;
        }

        // Jouer le son associé
        if (soundEvent != null) {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), soundEvent, SoundSource.PLAYERS, volume, pitch);
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
