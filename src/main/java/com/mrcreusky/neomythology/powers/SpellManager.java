package com.mrcreusky.neomythology.powers;

import com.mrcreusky.neomythology.powers.effects.DamageEffect;
import com.mrcreusky.neomythology.powers.effects.SpellEffect;
import com.mrcreusky.neomythology.powers.effects.StatusEffect;

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
        // Exemple d'enregistrement de sorts
        List<SpellEffect> lightBeamEffects = new ArrayList<>();
        lightBeamEffects.add(new DamageEffect(5.0F, new SpellAnimation("light_beam")));
        spells.put("light_beam", new Spell("Light Beam", 1, 30.0, lightBeamEffects, SpellType.DAMAGE, TargetType.ENTITY));

        List<SpellEffect> frostBoltEffects = new ArrayList<>();
        frostBoltEffects.add(new DamageEffect(3.0F, new SpellAnimation("frost")));
        frostBoltEffects.add(new StatusEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), new SpellAnimation("frost")));
        spells.put("frost_bolt", new Spell("Frost Bolt", 1, 25.0, frostBoltEffects, SpellType.UTILITY, TargetType.ENTITY));

        // List<SpellEffect> healingEffects = new ArrayList<>();
        // healingEffects.add(new HealingEffect(10.0F, new SpellAnimation("heal")));
        // spells.put("heal", new Spell("Heal", 50, 0.0, healingEffects, SpellType.SUPPORT, TargetType.SELF));

        List<SpellEffect> aoeEffects = new ArrayList<>();
        aoeEffects.add(new DamageEffect(3.0F, new SpellAnimation("explosion")));
        spells.put("explosion", new Spell("Explosion", 1, 15.0, aoeEffects, SpellType.AOE, TargetType.AREA));
    }

    public static Spell getSpell(String name) {
        return spells.get(name);
    }

    public static void registerSpell(Spell spell) {
        spells.put(spell.getName(), spell);
    }
}
