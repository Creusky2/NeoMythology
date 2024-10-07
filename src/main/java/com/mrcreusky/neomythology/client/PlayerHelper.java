package com.mrcreusky.neomythology.client;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class PlayerHelper {

    public static ServerPlayer getServerPlayerByName(MinecraftServer server, String playerName) {
        PlayerList playerList = server.getPlayerList();
        return playerList.getPlayerByName(playerName);
    }
}
