package com.mrcreusky.neomythology.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu;
import com.mrcreusky.neomythology.client.gui.QuestMenu;
import com.mrcreusky.neomythology.powers.CooldownManager;
import com.mrcreusky.neomythology.powers.Spell;
import com.mrcreusky.neomythology.powers.spells.LightBeamSpell;
import com.mrcreusky.neomythology.utils.RayTracingHelper;

import org.lwjgl.glfw.GLFW;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = "neomythology", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class KeyInputHandler {

    private static final Spell lightBeamSpell = new LightBeamSpell();
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
            GLFW.GLFW_KEY_L,
            "NeoMythology"
    );

    // Method for registering key bindings
    @SuppressWarnings("resource")
    public static void registerKeyBindings() {
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_GOD_SELECTION_MENU);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_QUEST_MENU);
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, CAST_FIRST_SPELL);
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
                // Ouvre le menu des quêtes
        if (CAST_FIRST_SPELL.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {

            ServerPlayer serverPlayer = PlayerHelper.getServerPlayerByName(minecraft.getSingleplayerServer(), minecraft.player.getName().getString());

            if (serverPlayer != null) {
                // Lancer le sort lorsqu'on appuie sur la touche associée
                if (CAST_FIRST_SPELL.isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) {
                    // Utiliser le ray tracing pour obtenir la position et la cible
                    HitResult hitResult = RayTracingHelper.getPlayerTarget(serverPlayer, serverPlayer.serverLevel(), 30);

                    // Si une cible a été trouvée
                    if (hitResult != null) {
                        Vec3 target = hitResult.getLocation();

                        // Vérifier si le sort peut être lancé
                        if (lightBeamSpell.canCast(serverPlayer, cooldownManager)) {
                            lightBeamSpell.cast(serverPlayer, serverPlayer.serverLevel(), target, cooldownManager);
                        } else {
                            serverPlayer.displayClientMessage(Component.literal("Spell is on cooldown!"), true);
                        }
                    }
                }
            }
        }
    }
}