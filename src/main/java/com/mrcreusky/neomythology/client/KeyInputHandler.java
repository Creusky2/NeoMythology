package com.mrcreusky.neomythology.client;

import com.mrcreusky.neomythology.client.gui.Menu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "neomythology", value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // Vérifie si une touche spécifique est appuyée (ici "G" par exemple)
        if (event.getKey() == GLFW.GLFW_KEY_G && event.getAction() == GLFW.GLFW_PRESS) {
            Minecraft.getInstance().setScreen(new Menu(Component.literal("NeoMythology")));
        }
    }
}