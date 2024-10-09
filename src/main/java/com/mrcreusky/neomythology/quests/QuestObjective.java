package com.mrcreusky.neomythology.quests;

public class QuestObjective {
    private String description;
    private int targetCount;  // Par exemple, le nombre de mobs à tuer
    private int currentCount; // Progrès actuel

    public QuestObjective(String description, int targetCount) {
        this.description = description;
        this.targetCount = targetCount;
        this.currentCount = 0;
    }

    public String getDescription() {
        return description;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void incrementProgress(int amount) {
        this.currentCount = Math.min(this.currentCount + amount, targetCount);
    }

    public boolean isComplete() {
        return currentCount >= targetCount;
    }
}
