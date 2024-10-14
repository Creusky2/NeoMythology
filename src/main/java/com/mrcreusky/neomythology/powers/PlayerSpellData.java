package com.mrcreusky.neomythology.powers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerSpellData {
    private static final String SPELLS_UNLOCKED_KEY = "SpellsUnlocked";
    private static final String SPELLS_EQUIPPED_KEY = "SpellsEquipped";

    private final List<String> spellsUnlocked;
    private final List<String> spellsEquipped;

    public PlayerSpellData() {
        this.spellsUnlocked = new ArrayList<>();
        this.spellsEquipped = new ArrayList<>(3);
        // Initialiser les slots équipés avec des valeurs vides
        for (int i = 0; i < 3; i++) {
            spellsEquipped.add("");
        }
    }

    // Méthode pour débloquer un sort
    public void unlockSpell(String spellName) {
        if (!spellsUnlocked.contains(spellName)) {
            spellsUnlocked.add(spellName);
        }
    }

    // Méthode pour équiper un sort dans un slot spécifique (0, 1, ou 2)
    public void equipSpell(String spellName, int slotIndex) {
        if (spellsUnlocked.contains(spellName) && slotIndex >= 0 && slotIndex < 3) {
            spellsEquipped.set(slotIndex, spellName);
        }
    }

    // Méthode pour déséquiper un sort d'un slot spécifique
    public void unequipSpell(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            spellsEquipped.set(slotIndex, "");
        }
    }

    // Vérifier si un sort est débloqué
    public boolean isSpellUnlocked(String spellName) {
        return spellsUnlocked.contains(spellName);
    }

    // Obtenir les sorts débloqués
    public List<String> getSpellsUnlocked() {
        return new ArrayList<>(spellsUnlocked);
    }

    // Obtenir les sorts équipés
    public List<String> getSpellsEquipped() {
        return new ArrayList<>(spellsEquipped);
    }

    // Obtenir le nom du sort équipé dans un slot spécifique
    public String getEquippedSpell(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < spellsEquipped.size()) {
            return spellsEquipped.get(slotIndex);
        }
        return "";
    }

    // Charger les données du joueur à partir d'un CompoundTag
    public void loadFromNBT(CompoundTag nbt) {
        spellsUnlocked.clear();
        ListTag unlockedTagList = nbt.getList(SPELLS_UNLOCKED_KEY, Tag.TAG_STRING);
        for (int i = 0; i < unlockedTagList.size(); i++) {
            spellsUnlocked.add(unlockedTagList.getString(i));
        }

        spellsEquipped.clear();
        ListTag equippedTagList = nbt.getList(SPELLS_EQUIPPED_KEY, Tag.TAG_STRING);
        for (int i = 0; i < 3; i++) {
            // Charger les sorts équipés, ou une valeur vide si absent
            String spellName = i < equippedTagList.size() ? equippedTagList.getString(i) : "";
            spellsEquipped.add(spellName);
        }
    }

    // Sauvegarder les données du joueur dans un CompoundTag
    public void saveToNBT(CompoundTag nbt) {
        ListTag unlockedTagList = new ListTag();
        for (String spellName : spellsUnlocked) {
            unlockedTagList.add(StringTag.valueOf(spellName)); // Utiliser StringTag.valueOf pour créer un StringTag
        }
        nbt.put(SPELLS_UNLOCKED_KEY, unlockedTagList);

        ListTag equippedTagList = new ListTag();
        for (String spellName : spellsEquipped) {
            equippedTagList.add(StringTag.valueOf(spellName)); // Utiliser StringTag.valueOf pour créer un StringTag
        }
        nbt.put(SPELLS_EQUIPPED_KEY, equippedTagList);
    }

    // Méthode statique pour obtenir les données du joueur
    public static PlayerSpellData getSpellData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG);
        PlayerSpellData spellData = new PlayerSpellData();
        spellData.loadFromNBT(persistentData);
        return spellData;
    }

    // Méthode statique pour sauvegarder les données du joueur
    public static void saveSpellData(ServerPlayer player, PlayerSpellData spellData) {
        CompoundTag persistentData = player.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG);
        spellData.saveToNBT(persistentData);
        player.getPersistentData().put(ServerPlayer.PERSISTED_NBT_TAG, persistentData);
    }

    // Débloquer un sort et l'équiper dans un slot spécifique
    public static void unlockAndEquipSpell(ServerPlayer player, String spellName, int slotIndex) {
        PlayerSpellData spellData = PlayerSpellData.getSpellData(player);
        spellData.unlockSpell(spellName);
        spellData.equipSpell(spellName, slotIndex);
        PlayerSpellData.saveSpellData(player, spellData);
    }

    // Vérifier si un sort est équipé dans un slot spécifique
    public static boolean isSpellEquipped(ServerPlayer player, String spellName) {
        PlayerSpellData spellData = PlayerSpellData.getSpellData(player);
        return spellData.getSpellsEquipped().contains(spellName);
    }
}
