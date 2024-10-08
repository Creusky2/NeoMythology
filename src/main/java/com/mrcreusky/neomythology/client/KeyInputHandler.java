package com.mrcreusky.neomythology.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu;
import org.lwjgl.glfw.GLFW;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.util.Lazy;


@EventBusSubscriber(modid = "neomythology", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class KeyInputHandler {

    // Key mapping for opening the God Selection Menu
    public static final KeyMapping OPEN_GOD_SELECTION_MENU = new KeyMapping(
            "Open God Selection Menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "NeoMythology"
    );

    // Method for registering key bindings
    @SuppressWarnings("resource")
    public static void registerKeyBindings() {
        Minecraft.getInstance().options.keyMappings = appendKeyBinding(Minecraft.getInstance().options.keyMappings, OPEN_GOD_SELECTION_MENU);
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
    }
}