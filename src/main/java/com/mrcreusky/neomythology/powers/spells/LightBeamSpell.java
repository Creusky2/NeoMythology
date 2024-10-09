package com.mrcreusky.neomythology.powers.spells;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import com.mrcreusky.neomythology.powers.CooldownManager;
import com.mrcreusky.neomythology.powers.Spell;
import com.mrcreusky.neomythology.utils.RayTracingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LightBeamSpell extends Spell {

    public LightBeamSpell() {
        // Nom du sort, cooldown de 100 ticks, et dégâts de 5 points
        super("Light Beam", 0, 5.0F);
    }

    @Override
    public void cast(ServerPlayer player, ServerLevel world, Vec3 target, CooldownManager cooldownManager) {
        // Vérifie si le sort peut être lancé
        if (!canCast(player, cooldownManager)) {
            player.displayClientMessage(Component.literal("Spell is on cooldown!"), true);
            return;
        }

        // Utilise le ray tracing pour obtenir la cible visée
        HitResult hitResult = RayTracingHelper.getPlayerTarget(player, world, 30); // 30 blocs de portée maximale

        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            // Si l'entité touchée est une entité vivante, lui infliger des dégâts
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                // Infliger des dégâts et vérifier si cela a fonctionné
                livingEntity.hurt(player.damageSources().magic(), getDamage());
            }
        } else {
            // Aucun bloc ou entité touché
            player.displayClientMessage(Component.literal("Aucune cible touchée."), true);
        }

        // Générer des particules le long du rayon
        Vec3 startVec = player.getEyePosition(1.0F);
        Vec3 direction = target.subtract(startVec).normalize();
        double distance = startVec.distanceTo(target);
        for (int i = 0; i < distance; i++) {
            Vec3 particlePos = startVec.add(direction.scale(i));
            // player.level().addParticle(ParticleTypes.END_ROD, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
            world.sendParticles(ParticleTypes.END_ROD, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
        }

        // Applique le cooldown
        cooldownManager.applyCooldown(player, this);
    }

}
