package com.mrcreusky.neomythology.utils;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.Entity;

public class RayTracingHelper {

    // Méthode pour obtenir la cible que le joueur vise avec une portée maximale
    public static HitResult getPlayerTarget(ServerPlayer player, ServerLevel world, double maxRange) {
        Vec3 startVec = player.getEyePosition(1.0F);  // Position des yeux du joueur
        Vec3 lookVec = player.getViewVector(1.0F);    // Direction du regard
        Vec3 endVec = startVec.add(lookVec.scale(maxRange));  // Calculer la fin du rayon

        // Ray tracing pour les blocs
        HitResult blockHit = world.clip(new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        // Ray tracing pour les entités
        HitResult entityHit = getEntityHit(player, world, startVec, endVec);

        // Si une entité est plus proche que le bloc, retourner l'entité comme cible
        if (entityHit != null && entityHit.getType() == HitResult.Type.ENTITY) {
            return entityHit;
        }

        return blockHit;  // Sinon retourner le bloc comme cible
    }

    // Ray tracing pour les entités (vérifie si le joueur vise une entité dans une portée définie)
    private static HitResult getEntityHit(ServerPlayer player, ServerLevel world, Vec3 startVec, Vec3 endVec) {
        EntityHitResult entityHit = null;
        double maxDistance = startVec.distanceTo(endVec);

        for (Entity entity : world.getEntities(player, player.getBoundingBox().inflate(maxDistance))) {
            if (entity != player && entity.getBoundingBox().clip(startVec, endVec).isPresent()) {
                entityHit = new EntityHitResult(entity);
            }
        }

        return entityHit;
    }
}
