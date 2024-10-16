package com.mrcreusky.neomythology.powers;

import com.mrcreusky.neomythology.powers.effects.DamageEffect;
import com.mrcreusky.neomythology.powers.effects.HealingEffect;
import com.mrcreusky.neomythology.powers.effects.SpellEffect;
import com.mrcreusky.neomythology.powers.effects.StatusEffect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import com.mrcreusky.neomythology.powers.Spell.SpellType;
import com.mrcreusky.neomythology.powers.Spell.TargetType;
import com.mrcreusky.neomythology.powers.animations.SpellAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellManager {
    private static final Map<String, Spell> spells = new HashMap<>();

    static {
        // Enregistrement de sorts
        List<SpellEffect> lightBeamEffects = new ArrayList<>();
        lightBeamEffects.add(new DamageEffect(5.0F, new SpellAnimation("light_beam", ResourceLocation.fromNamespaceAndPath("minecraft", "entity.lightning_bolt.thunder"), 1.0F, 1.0F)));
        spells.put("light_beam", new Spell("Light Beam", 1, 30.0, lightBeamEffects, SpellType.DAMAGE, TargetType.ENTITY));

        List<SpellEffect> frostBoltEffects = new ArrayList<>();
        frostBoltEffects.add(new DamageEffect(3.0F, new SpellAnimation("frost", null, 0, 0)));
        frostBoltEffects.add(new StatusEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), new SpellAnimation("frost", ResourceLocation.fromNamespaceAndPath("minecraft", "entity.player.levelup"), 1.0F, 1.0F)));
        spells.put("frost_bolt", new Spell("Frost Bolt", 1, 25.0, frostBoltEffects, SpellType.UTILITY, TargetType.ENTITY));

        // Sort de soin
        List<SpellEffect> healingEffects = new ArrayList<>();
        healingEffects.add(new HealingEffect(10.0F, new SpellAnimation("heal", ResourceLocation.fromNamespaceAndPath("minecraft", "entity.player.levelup"), 1.0F, 1.0F)));
        spells.put("heal", new Spell("Heal", 1, 0.0, healingEffects, SpellType.SUPPORT, TargetType.SELF));

        // Sort de résistance
        List<SpellEffect> resistanceEffects = new ArrayList<>();
        resistanceEffects.add(new StatusEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1), new SpellAnimation("resistance", ResourceLocation.fromNamespaceAndPath("minecraft", "entity.iron_golem.repair"), 1.0F, 1.0F)));
        spells.put("resistance", new Spell("Resistance", 1, 0.0, resistanceEffects, SpellType.SUPPORT, TargetType.SELF));

        List<SpellEffect> aoeEffects = new ArrayList<>();
        aoeEffects.add(new DamageEffect(3.0F, new SpellAnimation("explosion", ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.explode"), 1.0F, 1.0F)));
        spells.put("explosion", new Spell("Explosion", 1, 15.0, aoeEffects, SpellType.AOE, TargetType.AREA));
    }

    public static Spell getSpell(String name) {
        return spells.get(name);
    }

    public static void registerSpell(Spell spell) {
        spells.put(spell.getName(), spell);
    }

}
