package com.mrcreusky.neomythology.Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu;
import com.mrcreusky.neomythology.client.gui.GodSelectionMenu.God;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
// import net.neoforged.neoforge.client.event;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = "neomythology", bus = EventBusSubscriber.Bus.GAME)
public class ChatRenderer {

    private static Map<String, String> civilisationColors = new HashMap<>();

    // Charger les couleurs des civilisations depuis le fichier JSON
    static {
        loadCivilisations();
    }

    private static void loadCivilisations() {
        try {
            InputStream inputStream = ChatRenderer.class.getResourceAsStream("/data/neomythology/gods_config.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);

            JsonObject civilisationsJson = jsonObject.getAsJsonObject("civilisations");
            civilisationColors = new HashMap<>();

            for (String civilisation : civilisationsJson.keySet()) {
                JsonObject civData = civilisationsJson.getAsJsonObject(civilisation);
                String colorHex = civData.get("color").getAsString();
                civilisationColors.put(civilisation, colorHex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            civilisationColors = Map.of(); // Retourne une map vide en cas d'erreur
        }
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        Component originalMessage = event.getMessage();
        System.out.println(originalMessage.getSiblings().toString());
        System.out.println(originalMessage.getContents().toString());
        // originalMessage.getSiblings().forEach(component -> System.out.println("Sibling: " + component.getString()));

        System.out.println("Client chat received: " + originalMessage);

        if (player != null) {
            String selectedGodName = player.getPersistentData().getCompound("PlayerPersisted").getString("SelectedGod");
            GodSelectionMenu.reloadGods();
            God selectedGod = God.getGodByName(selectedGodName);
            if (selectedGod != null) {
                String civilisation = selectedGod.civilisation;
                String colorHex = civilisationColors.getOrDefault(civilisation, "#FFFFFF"); // Blanc par défaut
                System.out.println("Selected God: " + selectedGodName + " - Civilisation: " + civilisation + " - Color: " + colorHex);
                int color = Integer.parseInt(colorHex.substring(1), 16);

                // Appliquer la couleur au nom du joueur
                originalMessage.getStyle().withColor(color);
                PlayerChatMessage message = PlayerChatMessage.unsigned(player.getUUID(), originalMessage.getString());

                // // Envoyer le message personnalisé à tous les joueurs
                PlayerList playerList = player.getServer().getPlayerList();
                playerList.broadcastChatMessage(message, player, null);

                // Remplacer le message original par le message modifié
                event.setMessage(originalMessage);
                // event.setCanceled(true);
            }
        }
    }
}
