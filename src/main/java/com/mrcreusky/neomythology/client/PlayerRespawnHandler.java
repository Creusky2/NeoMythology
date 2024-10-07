package com.mrcreusky.neomythology.client;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import com.mrcreusky.neomythology.NeoMythology;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu.God;

@EventBusSubscriber(modid = NeoMythology.MODID)
public class PlayerRespawnHandler {

    // Méthode appelée lorsque le joueur est cloné (à sa mort et réapparition)
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ServerPlayer originalPlayer = (ServerPlayer) event.getOriginal();
        ServerPlayer newPlayer = (ServerPlayer) event.getEntity();

        System.out.println("Player cloned: " + originalPlayer.getName().getString() + " -> " + newPlayer.getName().getString());
        
        if (event.isWasDeath()) {
            System.out.println("Player died");
            System.out.println("Data : " + originalPlayer.getPersistentData().get("PlayerPersisted"));

            if (originalPlayer.getPersistentData().getCompound("PlayerPersisted").contains("SelectedGod") || newPlayer.getPersistentData().getCompound("PlayerPersisted").contains("SelectedGod")) {
                String selectedGodName = originalPlayer.getPersistentData().getCompound("PlayerPersisted").getString("SelectedGod");
                God selectedGod = God.getGodByName(selectedGodName);
                System.out.println("Selected God: " + selectedGodName);

                if (selectedGod != null) {
                    newPlayer.getPersistentData().putString("SelectedGod", selectedGodName);
                    PlayerDivinity.applyGodStatsToPlayer(selectedGod, newPlayer);
                }
            }
        }
    }

    // Utiliser PlayerLoggedInEvent pour appliquer les stats après la connexion
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (player.getPersistentData().getCompound("PlayerPersisted").contains("SelectedGod")) {
            String selectedGodName = player.getPersistentData().getCompound("PlayerPersisted").getString("SelectedGod");
            // GodSelectionMenu.reloadGods();

            // God selectedGod = God.getGodByName(selectedGodName);

            System.out.println("Player logged in: " + player.getName().getString() + " - Selected God: " + selectedGodName);

            // if (selectedGod != null) {
            //     PlayerDivinity.applyGodStatsToPlayer(selectedGod, player);
            // }
        }
    }
}
