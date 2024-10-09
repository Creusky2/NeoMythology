package com.mrcreusky.neomythology.quests;

import java.util.ArrayList;
import java.util.List;

import com.mrcreusky.neomythology.quests.Quest.QuestState;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerQuestData {

    // Sauvegarde les quêtes du joueur
    public static void saveQuestsToPlayer(ServerPlayer player, List<Quest> quests) {
        CompoundTag questTag = new CompoundTag();
        
        // Sauvegarder chaque quête
        for (Quest quest : quests) {
            CompoundTag questData = new CompoundTag();
            questData.putString("name", quest.getName());
            questData.putString("state", quest.getState().toString());

            // Sauvegarder les objectifs
            for (int i = 0; i < quest.getObjectives().size(); i++) {
                QuestObjective objective = quest.getObjectives().get(i);
                CompoundTag objectiveTag = new CompoundTag();
                objectiveTag.putString("description", objective.getDescription());
                objectiveTag.putInt("targetCount", objective.getTargetCount());
                objectiveTag.putInt("currentCount", objective.getCurrentCount());
                
                questData.put("objective_" + i, objectiveTag);
            }

            questTag.put(quest.getName(), questData);
        }
        
        player.getPersistentData().put("PlayerQuests", questTag);
    }


    // Charge les quêtes du joueur depuis les données persistantes
    public static List<Quest> loadQuestsFromPlayer(ServerPlayer player) {
        CompoundTag questTag = player.getPersistentData().getCompound("PlayerQuests");
        List<Quest> quests = new ArrayList<>();

        // Charger les quêtes à partir du tag
        for (String questKey : questTag.getAllKeys()) {
            CompoundTag questData = questTag.getCompound(questKey);
            String name = questData.getString("name");
            QuestState state = QuestState.valueOf(questData.getString("state"));

            // Charger les objectifs
            List<QuestObjective> objectives = new ArrayList<>();
            for (int i = 0; questData.contains("objective_" + i); i++) {
                CompoundTag objectiveTag = questData.getCompound("objective_" + i);
                String description = objectiveTag.getString("description");
                int targetCount = objectiveTag.getInt("targetCount");
                int currentCount = objectiveTag.getInt("currentCount");

                QuestObjective objective = new QuestObjective(description, targetCount);
                objective.incrementProgress(currentCount); // Charger la progression
                objectives.add(objective);
            }

            Quest quest = new Quest(name);
            quest.getObjectives().addAll(objectives);
            quest.setState(state);
            quests.add(quest);
        }

        return quests;
    }

    // Check if player has a specific quest
    public static boolean hasQuest(ServerPlayer player, String questName) {
        List<Quest> quests = loadQuestsFromPlayer(player);
        return quests.stream().anyMatch(quest -> quest.getName().equals(questName));
    }

    // Add any quest to the player
    public static void addQuestToPlayer(ServerPlayer player, Quest quest) {
        // Load the player's quests
        List<Quest> quests = loadQuestsFromPlayer(player);

        // Check if the quest already exists, if not add it
        if (!hasQuest(player, quest.getName())) {
            quests.add(quest);
            saveQuestsToPlayer(player, quests);  // Save the updated quest list
        }
    }

    // Complete a quest for the player
    public static void completeQuest(ServerPlayer player, String questName) {
        List<Quest> quests = loadQuestsFromPlayer(player);
        for (Quest quest : quests) {
            if (quest.getName().equals(questName)) {
                for(QuestObjective objective : quest.getObjectives()) {
                    objective.incrementProgress(objective.getTargetCount()); // Mark all objectives as complete
                }
                quest.complete(); // Mark the quest as completed
                break;
            }
        }
        saveQuestsToPlayer(player, quests);
    }

}
