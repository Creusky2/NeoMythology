package com.mrcreusky.neomythology.powers;

import com.mrcreusky.neomythology.powers.effects.SpellEffect;
import com.mrcreusky.neomythology.utils.RayTracingHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class Spell {
    private String name;
    private int cooldown;
    private double range;
    private List<SpellEffect> effects;
    private SpellType type; // Type de sort (Damage, AOE, etc.)
    private TargetType targetType; // Type de cible (Self, Entity, Area)

    public enum SpellType {
        DAMAGE,  // Sort de type dommage
        SUPPORT, // Sort de soutien (buffs, soins, etc.)
        UTILITY, // Sort utilitaire (téléportation, invisibilité, etc.)
        AOE      // Sort de zone
    }

    public enum TargetType {
        SELF,      // Le lanceur lui-même
        ENTITY,    // Une entité spécifique
        AREA       // Une zone (pour les sorts AOE)
    }

    public Spell(String name, int cooldown, double range, List<SpellEffect> effects, SpellType type, TargetType targetType) {
        this.name = name;
        this.cooldown = cooldown;
        this.range = range;
        this.effects = effects;
        this.type = type;
        this.targetType = targetType;
    }

    // Getters et setters pour le nom
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters et setters pour le cooldown
    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    // Getters et setters pour la portée
    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    // Getters et setters pour les effets
    public List<SpellEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<SpellEffect> effects) {
        this.effects = effects;
    }

    // Getters et setters pour le type de sort
    public SpellType getType() {
        return type;
    }

    public void setType(SpellType type) {
        this.type = type;
    }

    // Getters et setters pour le type de cible
    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    // Méthode pour lancer le sort
    public void cast(ServerPlayer player, ServerLevel world, Vec3 target, CooldownManager cooldownManager) {
        if (!canCast(player, cooldownManager)) {
            player.displayClientMessage(Component.literal("Spell is on cooldown!"), true);
            return;
        }

        switch (targetType) {
            case SELF:
                // Appliquer les effets sur le joueur lui-même
                applyEffects(player, player);
                break;

            case ENTITY:
                // Utiliser le ray tracing pour déterminer la cible
                HitResult hitResult = RayTracingHelper.getPlayerTarget(player, world, range);
                if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                    Entity entity = entityHitResult.getEntity();
                    if (entity instanceof LivingEntity) {
                        applyEffects(player, (LivingEntity) entity);
                    } else {
                        player.displayClientMessage(Component.literal("La cible n'est pas une entité valide."), true);
                    }
                } else {
                    player.displayClientMessage(Component.literal("Aucune entité trouvée dans la portée."), true);
                }
                break;

            case AREA:
                // Appliquer les effets à tous les ennemis dans une zone autour du point cible
                applyEffectsInArea(player, world, target);
                break;

            default:
                player.displayClientMessage(Component.literal("Type de cible non géré."), true);
                break;
        }

        cooldownManager.applyCooldown(player, this);
    }

    // Appliquer les effets à une entité cible
    private void applyEffects(ServerPlayer caster, LivingEntity target) {
        for (SpellEffect effect : effects) {
            effect.applyEffect(caster, target);
        }
    }

    // Appliquer les effets dans une zone
    private void applyEffectsInArea(ServerPlayer caster, ServerLevel world, Vec3 center) {
        double areaRadius = this.range;
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(areaRadius));
        for (LivingEntity entity : entities) {
            if (entity != caster) { // Ne pas affecter le lanceur lui-même
                applyEffects(caster, entity);
            }
        }
    }

    // Vérifie si le sort peut être lancé
    public boolean canCast(ServerPlayer player, CooldownManager cooldownManager) {
        return !cooldownManager.isOnCooldown(player, this);
    }
}
