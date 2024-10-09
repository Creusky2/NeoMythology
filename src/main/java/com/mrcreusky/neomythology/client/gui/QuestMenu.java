package com.mrcreusky.neomythology.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import com.mrcreusky.neomythology.quests.Quest;
import com.mrcreusky.neomythology.quests.QuestObjective;
import com.mrcreusky.neomythology.quests.PlayerQuestData;
import net.minecraft.server.level.ServerPlayer;
import com.mrcreusky.neomythology.client.PlayerHelper;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class QuestMenu extends Screen {
    private List<QuestSection> sections;

    @SuppressWarnings("resource")
    public QuestMenu() {
        super(Component.literal("Quest Menu"));

        // Load player's quests
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        ServerPlayer player = PlayerHelper.getServerPlayerByName(server, localPlayer.getName().getString());

        // Load the player's quests
        List<Quest> playerQuests = PlayerQuestData.loadQuestsFromPlayer(player);

        // Create sections
        sections = new ArrayList<>();
        sections.add(new QuestSection("Quêtes non commencées", playerQuests, Quest.QuestState.NOT_STARTED));
        sections.add(new QuestSection("Quêtes en cours", playerQuests, Quest.QuestState.IN_PROGRESS));
        sections.add(new QuestSection("Quêtes terminées", playerQuests, Quest.QuestState.COMPLETED));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int yPosition = 50;
        for (QuestSection section : sections) {
            yPosition = section.render(guiGraphics, mouseX, mouseY, yPosition);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left-click
            int yPosition = 50; // Start at the same yPosition as in render
            for (QuestSection section : sections) {
                if (section.mouseClicked(mouseX, mouseY, yPosition)) {
                    playButtonClickSound();
                    return true; // If we handled the click, stop further processing
                }
                // Adjust yPosition after rendering each section
                yPosition += section.getSectionHeight();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playButtonClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}

/**
 * Represents a section of quests, like "Not Started" or "In Progress".
 */
class QuestSection {
    private String title;
    private List<QuestObject> quests;
    private boolean isExpanded;

    public QuestSection(String title, List<Quest> quests, Quest.QuestState questState) {
        this.title = title;
        this.quests = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.getState() == questState) {
                this.quests.add(new QuestObject(quest));
            }
        }
        this.isExpanded = true; // Start expanded by default
    }

    @SuppressWarnings("resource")
    public int render(GuiGraphics guiGraphics, int mouseX, int mouseY, int yPosition) {
        // Draw section title
        boolean hovered = isMouseOver(mouseX, mouseY, 20, yPosition, 200, 20);
        int color = hovered ? 0xFFDDDDDD : 0xFFAAAAAA;
        guiGraphics.fill(20, yPosition - 5, 220, yPosition + 15, hovered ? 0x50000000 : 0x30000000);
        guiGraphics.drawString(Minecraft.getInstance().font, (isExpanded ? " ^ " : " v ") + title, 20, yPosition, color);

        yPosition += 20;

        // Render quests if expanded
        if (isExpanded) {
            for (QuestObject questObject : quests) {
                yPosition = questObject.render(guiGraphics, mouseX, mouseY, yPosition);
            }
        }

        return yPosition;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int yPosition) {
        // Check if section title is clicked to expand/collapse
        if (isMouseOver(mouseX, mouseY, 20, yPosition, 200, 20)) {
            isExpanded = !isExpanded;
            return true;
        }

        yPosition += 20; // Adjust yPosition for the quest list

        // Check if any quest inside the section was clicked
        if (isExpanded) {
            for (QuestObject questObject : quests) {
                if (questObject.mouseClicked(mouseX, mouseY, yPosition)) {
                    return true;
                }
                yPosition += questObject.getHeight(); // Adjust yPosition after each quest
            }
        }

        return false;
    }

    public int getSectionHeight() {
        int height = 20; // Base height for the section header
        if (isExpanded) {
            for (QuestObject questObject : quests) {
                height += questObject.getHeight();
            }
        }
        return height;
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY < (y + height);
    }
}

/**
 * Represents an individual quest and its objectives.
 */
class QuestObject {
    private Quest quest;
    private boolean showObjectives;

    public QuestObject(Quest quest) {
        this.quest = quest;
        this.showObjectives = false; // Start collapsed
    }

    @SuppressWarnings("resource")
    public int render(GuiGraphics guiGraphics, int mouseX, int mouseY, int yPosition) {
        // Draw quest title
        boolean hovered = isMouseOver(mouseX, mouseY, 40, yPosition, 260, 15);
        int color = hovered ? 0xFFDDDDDD : 0xFFFFFF;
        guiGraphics.fill(35, yPosition - 5, 300, yPosition + 10, hovered ? 0x50000000 : 0x30000000);
        guiGraphics.drawString(Minecraft.getInstance().font, quest.getName() + (showObjectives ? " [-]" : " [+]"), 40, yPosition, color);

        yPosition += 15;

        // Render objectives if expanded
        if (showObjectives) {
            for (QuestObjective objective : quest.getObjectives()) {
                yPosition = renderObjective(guiGraphics, objective, yPosition);
            }
        }

        return yPosition + 10; // Extra spacing after each quest
    }

    public boolean mouseClicked(double mouseX, double mouseY, int yPosition) {
        // Toggle objective visibility if quest title is clicked
        if (isMouseOver(mouseX, mouseY, 40, yPosition, 260, 15)) {
            showObjectives = !showObjectives;
            return true;
        }
        return false;
    }

    public int getHeight() {
        int height = 25; // Base height for quest title
        if (showObjectives) {
            height += 20 * quest.getObjectives().size(); // Add space for each objective
        }
        return height;
    }

    @SuppressWarnings("resource")
    private int renderObjective(GuiGraphics guiGraphics, QuestObjective objective, int yPosition) {
        int progress = objective.getCurrentCount();
        int target = objective.getTargetCount();

        // Draw objective description
        guiGraphics.drawString(Minecraft.getInstance().font, objective.getDescription() + " (" + progress + "/" + target + ")", 60, yPosition, 0xAAAAAA);

        // Draw progress bar
        int barWidth = 100;
        int filledWidth = (int) ((double) progress / target * barWidth);
        int barX = 60;
        int barY = yPosition + 10;

        // Draw white border and progress bar
        guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + 7, 0xFFFFFFFF); // White border
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 6, 0xFF555555); // Empty bar
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + 6, 0xFF00FF00); // Filled progress

        return yPosition + 20;
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY < (y + height);
    }
}
