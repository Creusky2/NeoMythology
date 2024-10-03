package com.mrcreusky.neomythology.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.util.Mth;


public class Menu extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/gods_selection_menu.png");

    // Tableau des dieux (associé aux textures de chaque dieu)
    private static final ResourceLocation[] GOD_ICONS = {
        ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_zeus.png"),
        ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_hades.png"),
        ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_poseidon.png"),
        // Ajoute plus de dieux ici...
    };

    // Pour l'affichage des détails à droite
    private String selectedGodName = "Zeus";  // Par défaut, on sélectionne Zeus
    private String godDescription = "Minor Gods and God Powers";  // Description par défaut
    private static final ResourceLocation GOD_DETAILS_ICON = ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/details_thor.png");

    public Menu(Component title) {
        super(title);
    }

    protected void init() {
        int buttonWidth = 64;
        int buttonHeight = 64;
        int padding = 10;
        int startX = this.width / 2 - (3 * (buttonWidth + padding)) / 2;  // Centrer la grille
        int startY = this.height / 4;

        // Ajouter les boutons avec icônes des dieux
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int x = startX + col * (buttonWidth + padding);
                int y = startY + row * (buttonHeight + padding);
                int index = row * 3 + col;

                if (index < GOD_ICONS.length) {
                    ResourceLocation godIcon = GOD_ICONS[index];
                    this.addRenderableWidget(new ImageButton(x, y, buttonWidth, buttonHeight, 0, 0, buttonHeight, godIcon, buttonWidth, buttonHeight * 2, button -> {
                        // Action lors de la sélection d'un dieu
                        this.selectedGodName = "God " + (index + 1);  // Nom temporaire, remplace par le nom réel
                        this.godDescription = "Description of God " + (index + 1);  // Description temporaire
                    }));
                }
            }
        }

        // Ajouter un bouton pour la sélection aléatoire
        this.addRenderableWidget(new Button(this.width / 2 - 50, startY + 3 * (buttonHeight + padding), 100, 20, Component.literal("Random"), button -> {
            // Action lors de la sélection aléatoire
            int randomGod = Mth.randomBetweenInclusive(this.random, 0, GOD_ICONS.length - 1);
            this.selectedGodName = "God " + (randomGod + 1);
            this.godDescription = "Randomly selected God " + (randomGod + 1);
        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // Dessiner l'arrière-plan
        renderBackground(poseStack);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        blit(poseStack, 0, 0, 0, 0, this.width, this.height);

        // Dessiner la partie droite (informations sur le dieu sélectionné)
        drawRightPanel(poseStack);

        // Dessiner les boutons et les composants de base
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void drawRightPanel(PoseStack poseStack) {
        // Afficher les détails du dieu sélectionné
        int infoX = this.width / 2 + 100;
        int infoY = this.height / 4;

        // Afficher le nom du dieu
        drawString(poseStack, this.font, this.selectedGodName, infoX, infoY, 0xFFFFFF);
        drawString(poseStack, this.font, this.godDescription, infoX, infoY + 20, 0xFFFFFF);

        // Dessiner une icône (détails supplémentaires)
        RenderSystem.setShaderTexture(0, GOD_DETAILS_ICON);
        blit(poseStack, infoX, infoY + 40, 0, 0, 64, 64, 64, 64);
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        // Ici on pourrait ajouter une texture d'arrière-plan personnalisée
        super.renderBackground(poseStack);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Indique si l'écran met le jeu en pause
    }
}
