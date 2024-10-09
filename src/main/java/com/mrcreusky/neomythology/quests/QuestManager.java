package com.mrcreusky.neomythology.quests;

import java.util.List;

import com.mrcreusky.neomythology.quests.Quest.QuestState;

public class QuestManager {
    // Met à jour la progression d'une quête
    public static void updateQuestProgress(Quest quest, String objectiveDescription, int progress) {
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective.getDescription().equals(objectiveDescription) && !objective.isComplete()) {
                objective.incrementProgress(progress);
            }
        }
        quest.updateQuestState();
    }

    // Vérifie si toutes les quêtes d'un joueur sont complètes
    public static boolean allQuestsCompleted(List<Quest> quests) {
        for (Quest quest : quests) {
            if (quest.getState() != QuestState.COMPLETED) {
                return false;
            }
        }
        return true;
    }
}
