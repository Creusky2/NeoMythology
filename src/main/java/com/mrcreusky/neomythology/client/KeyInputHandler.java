package com.mrcreusky.neomythology.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu;
import com.mrcreusky.neomythology.client.gui.QuestMenu;
import com.mrcreusky.neomythology.powers.CooldownManager;
import com.mrcreusky.neomythology.powers.Spell;
import com.mrcreusky.neomythology.powers.SpellManager;
import com.mrcreusky.neomythology.powers.Spell.TargetType;
import com.mrcreusky.neomythology.utils.RayTracingHelper;

import org.lwjgl.glfw.GLFW;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.api.distmarker.Dist;

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

    public static final KeyMapping CAST_FIRST_SPELL = new KeyMapping(
            "Cast First Spell",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "NeoMythology"
    );

    public static final KeyMapping CAST_SECOND_SPELL = new KeyMapping(
            "Cast Second Spell",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "NeoMythology"
    );

    public static final KeyMapping CAST_THIRD_SPELL = new KeyMapping(
            "Cast Third Spell",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "NeoMythology"
    );

    // Method for registering key bindings
    @SuppressWarnings("resource")
    public static void registerKeyBindings() {
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_GOD_SELECTION_MENU);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_QUEST_MENU);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_FIRST_SPELL);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_SECOND_SPELL);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_THIRD_SPELL);
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
                minecraft.setScreen(new GodSelectionMenu());
            }
        }

        // Ouvre le menu des quêtes
        if (OPEN_QUEST_MENU.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            if (minecraft.screen == null) {
                minecraft.setScreen(new QuestMenu());
            }
        }

        // Lancer les sorts lorsqu'une touche est associée à un sort
        if (CAST_FIRST_SPELL.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            castSpell("resistance", minecraft);
        }
        if (CAST_SECOND_SPELL.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            castSpell("frost_bolt", minecraft);
        }
        if (CAST_THIRD_SPELL.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
            castSpell("explosion", minecraft);
        }
    }

    private static void castSpell(String spellName, Minecraft minecraft) {
        ServerPlayer serverPlayer = PlayerHelper.getServerPlayerByName(minecraft.getSingleplayerServer(), minecraft.player.getName().getString());

        if (serverPlayer != null) {
            Spell spell = SpellManager.getSpell(spellName);

            if (spell != null) {
                // Vérifier le type de cible du sort
                if (spell.getTargetType() == TargetType.SELF || spell.getTargetType() == TargetType.AREA) {
                    // Les sorts SELF ou AREA n'ont pas besoin d'une cible spécifique
                    Vec3 target = serverPlayer.position(); // Utiliser la position du joueur comme centre
                    if (spell.canCast(serverPlayer, cooldownManager)) {
                        spell.cast(serverPlayer, serverPlayer.serverLevel(), target, cooldownManager);
                    } else {
                        serverPlayer.displayClientMessage(Component.literal("Spell is on cooldown!"), true);
                    }
                } else {
                    // Pour les autres types de cible, utiliser le ray tracing pour déterminer la cible
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
        }
    }
}
