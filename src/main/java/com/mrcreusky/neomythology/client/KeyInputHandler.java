package com.mrcreusky.neomythology.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrcreusky.neomythology.client.gui.GodMenu;
import com.mrcreusky.neomythology.client.gui.QuestMenu;
import com.mrcreusky.neomythology.powers.CooldownManager;
import com.mrcreusky.neomythology.powers.PlayerSpellData;
import com.mrcreusky.neomythology.powers.Spell;
import com.mrcreusky.neomythology.powers.SpellManager;
import com.mrcreusky.neomythology.powers.Spell.TargetType;
import com.mrcreusky.neomythology.utils.RayTracingHelper;

import org.lwjgl.glfw.GLFW;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.api.distmarker.Dist;

import java.util.List;

@EventBusSubscriber(modid = "neomythology", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class KeyInputHandler {

    private static final CooldownManager cooldownManager = new CooldownManager(); // Cooldown manager instance

    // Key mapping for opening the God Selection Menu
    public static final KeyMapping OPEN_GOD_SELECTION_MENU = new KeyMapping(
            "Open God Selection Menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "NeoMythology"
    );

    // Key mapping for opening the Quest Menu
    public static final KeyMapping OPEN_QUEST_MENU = new KeyMapping(
            "Open Quest Menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J, // Par exemple, la touche 'J' pour les quêtes
            "NeoMythology"
    );

    // Key mappings for casting spells from slots 1, 2, and 3
    public static final KeyMapping CAST_SPELL_SLOT_1 = new KeyMapping(
            "Cast Spell Slot 1",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "NeoMythology"
    );

    public static final KeyMapping CAST_SPELL_SLOT_2 = new KeyMapping(
            "Cast Spell Slot 2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "NeoMythology"
    );

    public static final KeyMapping CAST_SPELL_SLOT_3 = new KeyMapping(
            "Cast Spell Slot 3",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "NeoMythology"
    );

    // Method for registering key bindings
    @SuppressWarnings("resource")
    public static void registerKeyBindings() {
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_GOD_SELECTION_MENU);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_QUEST_MENU);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_SPELL_SLOT_1);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_SPELL_SLOT_2);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_SPELL_SLOT_3);
    }

    // Utility method to add key bindings ensuring a single unique value
    private static KeyMapping[] appendKeyBinding(KeyMapping[] existingBindings, KeyMapping newBinding) {
        for (KeyMapping binding : existingBindings) {
            if (binding.equals(newBinding)) {
                return existingBindings; // If binding already exists, return existing array
            }
        }
        KeyMapping[] newArray = new KeyMapping[existingBindings.length + 1];
        System.arraycopy(existingBindings, 0, newArray, 0, existingBindings.length);
        newArray[existingBindings.length] = newBinding;
        return newArray;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();

        // Vérifie si la touche configurée est appuyée
        if (OPEN_GOD_SELECTION_MENU.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            if (minecraft.screen == null) {
                minecraft.setScreen(new GodMenu());
            }
        }

        // Ouvre le menu des quêtes
        if (OPEN_QUEST_MENU.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            if (minecraft.screen == null) {
                minecraft.setScreen(new QuestMenu());
            }
        }

        // Lancer les sorts équipés
        if (CAST_SPELL_SLOT_1.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            castEquippedSpell(0, minecraft); // Slot 1 correspond à l'index 0
        }
        if (CAST_SPELL_SLOT_2.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            castEquippedSpell(1, minecraft); // Slot 2 correspond à l'index 1
        }
        if (CAST_SPELL_SLOT_3.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            castEquippedSpell(2, minecraft); // Slot 3 correspond à l'index 2
        }
    }

    // Méthode pour lancer un sort à partir de l'un des slots équipés
    private static void castEquippedSpell(int slotIndex, Minecraft minecraft) {
        ServerPlayer serverPlayer = PlayerHelper.getServerPlayerByName(minecraft.getSingleplayerServer(), minecraft.player.getName().getString());

        if (serverPlayer != null) {
            PlayerSpellData spellData = PlayerSpellData.getSpellData(serverPlayer);
            List<String> equippedSpells = spellData.getSpellsEquipped();

            // Vérifier si le slot est valide
            if (slotIndex < equippedSpells.size()) {
                String spellName = equippedSpells.get(slotIndex);
                Spell spell = SpellManager.getSpell(spellName);

                if (spell != null) {
                    if (spell.getTargetType() == TargetType.SELF || spell.getTargetType() == TargetType.AREA) {
                        Vec3 target = serverPlayer.position();
                        if (spell.canCast(serverPlayer, cooldownManager)) {
                            spell.cast(serverPlayer, serverPlayer.serverLevel(), target, cooldownManager);
                        } else {
                            serverPlayer.displayClientMessage(Component.literal("Spell is on cooldown!"), true);
                        }
                    } else {
                        HitResult hitResult = RayTracingHelper.getPlayerTarget(serverPlayer, serverPlayer.serverLevel(), spell.getRange());
                        if (hitResult != null) {
                            Vec3 target = hitResult.getLocation();
                            if (spell.canCast(serverPlayer, cooldownManager)) {
                                spell.cast(serverPlayer, serverPlayer.serverLevel(), target, cooldownManager);
                            } else {
                                serverPlayer.displayClientMessage(Component.literal("Spell is on cooldown!"), true);
                            }
                        } else {
                            serverPlayer.displayClientMessage(Component.literal("Aucune cible trouvée pour le sort."), true);
                        }
                    }
                } else {
                    serverPlayer.displayClientMessage(Component.literal("Le sort spécifié n'existe pas."), true);
                }
            } else {
                serverPlayer.displayClientMessage(Component.literal("Aucun sort équipé dans ce slot."), true);
            }
        }
    }
}
