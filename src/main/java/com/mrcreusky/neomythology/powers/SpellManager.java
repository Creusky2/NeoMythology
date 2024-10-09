package com.mrcreusky.neomythology.powers;

import com.mrcreusky.neomythology.powers.spells.LightBeamSpell;

import java.util.HashMap;
import java.util.Map;

public class SpellManager {
    private static final Map<String, Spell> spells = new HashMap<>();

    static {
        spells.put("light_beam", new LightBeamSpell());
        // spells.put("fireball", new FireballSpell());
        // Ajouter plus de sorts ici
    }

    public static Spell getSpell(String name) {
        return spells.get(name);
    }

    public static void registerSpell(Spell spell) {
        spells.put(spell.getName(), spell);
    }
}
