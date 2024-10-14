package com.mrcreusky.neomythology.client;

import com.mrcreusky.neomythology.client.gui.GodMenu.God;


public class PlayerDivinity {
    private God selectedGod; // La divinité choisie par le joueur
    private int level;       // Niveau de divinité du joueur
    private int experience;  // Expérience en tant que divinité

    // private static float BASE_HEALTH = 20.0f;
    // private static float BASE_ATTACK = 1.0f;
    // private static float BASE_DEFENSE = 1.0f;
    // private static float BASE_SPEED = 1.0f;

    public PlayerDivinity(God selectedGod) {
        this.selectedGod = selectedGod;
        this.level = 1;
        this.experience = 0;
    }

    public God getSelectedGod() {
        return selectedGod;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public void addExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }

    // Méthode pour vérifier si le joueur doit monter de niveau
    private void checkLevelUp() {
        int experienceRequired = 100 * level; // Exemple : chaque niveau demande 100 * niveau points d'expérience
        if (this.experience >= experienceRequired) {
            this.level++;
            this.experience -= experienceRequired;
            // improveStats();
            // Débloquer des compétences ici, si nécessaire
        }
    }
}


    // private static void test(Holder<Attribute> attributeHolder, ServerPlayer player, God god) {
    //                 AttributeInstance attribute = player.getAttribute(attributeHolder);
    //         if (attribute != null) {
    //             attribute.addPermanentModifier(new AttributeModifier(
    //                 ResourceLocation.fromNamespaceAndPath("neomythology", "god_health_modifier"),
    //                 god.stats.getHealth(),
    //                 AttributeModifier.Operation.ADD_VALUE
    //             ));
    //         }
    // }

