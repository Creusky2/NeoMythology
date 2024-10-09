package com.mrcreusky.neomythology.quests;

import net.minecraft.server.level.ServerPlayer;

public class DefaultQuests {

    public static void addChooseDivinityQuestToPlayer(ServerPlayer player) {
        Quest chooseDivinityQuest = new Quest("Choose a Divinity");
        QuestObjective chooseDivinityObjective = new QuestObjective("Select a God", 1);
        chooseDivinityQuest.addObjective(chooseDivinityObjective);

        // Add the quest to the player
        PlayerQuestData.addQuestToPlayer(player, chooseDivinityQuest);
    }

}