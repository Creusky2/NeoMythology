package com.mrcreusky.neomythology.quests;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private String name;
    private QuestState state;
    private List<QuestObjective> objectives;

    public enum QuestState {
        NOT_STARTED, // Quête non commencée
        IN_PROGRESS, // Quête en cours
        COMPLETED    // Quête terminée
    }

    public Quest(String name) {
        this.name = name;
        this.state = QuestState.NOT_STARTED; // Par défaut, la quête est non commencée
        this.objectives = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public void start() {
        this.state = QuestState.IN_PROGRESS;
    }

    public void complete() {
        this.state = QuestState.COMPLETED;
    }

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    public void addObjective(QuestObjective objective) {
        objectives.add(objective);
    }

    public boolean isCompleted() {
        return this.state == QuestState.COMPLETED;
    }

    public boolean isNotStarted() {
        return this.state == QuestState.NOT_STARTED;
    }

    public boolean isInProgress() {
        return this.state == QuestState.IN_PROGRESS;
    }

    /**
     * Met à jour l'état de la quête en fonction de l'avancement des objectifs
     */
    public void updateQuestState() {
        boolean allObjectivesCompleted = true;
        boolean anyObjectiveInProgress = false;

        // Parcourir tous les objectifs de la quête
        for (QuestObjective objective : objectives) {
            if (objective.isComplete()) {
                continue; // Cet objectif est terminé
            }
            
            if (objective.getCurrentCount() > 0) {
                anyObjectiveInProgress = true; // Au moins un objectif a progressé
            }

            // Si un seul objectif n'est pas encore complété, la quête ne peut pas être marquée comme complétée
            allObjectivesCompleted = false;
        }

        // Mettre à jour l'état de la quête en fonction des objectifs
        if (allObjectivesCompleted) {
            this.state = QuestState.COMPLETED;
        } else if (anyObjectiveInProgress) {
            this.state = QuestState.IN_PROGRESS;
        } else {
            this.state = QuestState.NOT_STARTED;
        }
    }
}
