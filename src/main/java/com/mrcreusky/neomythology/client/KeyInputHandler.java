package com.mrcreusky.neomythology.client;

import net.minecraft.client.Minecraft;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu;
import org.lwjgl.glfw.GLFW;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = "neomythology", value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // Vérifie si une touche spécifique est appuyée (ici "G" par exemple)
        
        if (event.getKey() == GLFW.GLFW_KEY_G && event.getAction() == GLFW.GLFW_PRESS) {
            Minecraft.getInstance().setScreen(new GodSelectionMenu());
        }
    }
}