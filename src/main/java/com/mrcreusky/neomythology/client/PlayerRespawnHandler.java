package com.mrcreusky.neomythology.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import com.mrcreusky.neomythology.NeoMythology;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu.God;

@EventBusSubscriber(modid = NeoMythology.MODID)
public class PlayerRespawnHandler {

    // Méthode appelée lorsque le joueur est cloné (à sa mort et réapparition)
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ServerPlayer originalPlayer = (ServerPlayer) event.getOriginal();
        ServerPlayer newPlayer = (ServerPlayer) event.getEntity();

        if (event.isWasDeath()) {
            
            if (God.hasSelectedGod(originalPlayer) || God.hasSelectedGod(newPlayer)) {
                God.getFromPlayer(originalPlayer).applyGodStatsToPlayer(newPlayer);
            }
        }
    }

    // Utiliser PlayerLoggedInEvent pour ouvrir le menu de sélection s'il n'a pas choisi de dieu
    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!God.hasSelectedGod(player)) {
            System.out.println("Player logged in: " + player.getName().getString() + " - No god selected");
            // Exécuter sur le client (retarder un peu pour s'assurer que tout est chargé)
            Minecraft.getInstance().tell(() -> {
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(player.getUUID())) {
                    Minecraft.getInstance().setScreen(new GodSelectionMenu());
                }
            });
        }
    }
}
