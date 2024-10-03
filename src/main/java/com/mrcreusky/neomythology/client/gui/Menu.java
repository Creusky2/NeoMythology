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
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.Button.CreateNarration;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;


public class Menu extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/gods_selection_menu.png");

    WidgetSprites godIconSprites[] = {
        new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_zeus.png"), // Emplacement de la texture
            ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_zeus_hovered.png")  // Optionnel : texture lorsqu'on survole
        ),
        new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_hades.png"), // Emplacement de la texture
            ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_hades_hovered.png")  // Optionnel : texture lorsqu'on survole
        ),
        new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_poseidon.png"), // Emplacement de la texture
            ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/icon_poseidon_hovered.png")  // Optionnel : texture lorsqu'on survole
        )
    };

    // Pour l'affichage des détails à droite
    private String selectedGodName = "Zeus";  // Par défaut, on sélectionne Zeus
    private String godDescription = "Minor Gods and God Powers";  // Description par défaut
    private static final ResourceLocation GOD_DETAILS_ICON = ResourceLocation.fromNamespaceAndPath("neomythology", "textures/gui/details_thor.png");

    public Menu(Component title) {
        super(title);
    }

    protected void init() {
        RandomSource random = RandomSource.create();
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

                if (index < godIconSprites.length) {
                    WidgetSprites godIcon = godIconSprites[index];
                    this.addRenderableWidget(new ImageButton(x, y, buttonWidth, buttonHeight, godIcon, button -> {
                        // Action lors de la sélection d'un dieu
                        this.selectedGodName = "God " + (index + 1);  // Nom temporaire, remplace par le nom réel
                        this.godDescription = "Description of God " + (index + 1);  // Description temporaire
                    }));
                }                
            }
        }

        this.addRenderableWidget(new Button.Builder(Component.literal("Random"), 
            button -> {
                // Action lors de la sélection aléatoire
                int randomGod = Mth.randomBetweenInclusive(random, 0, godIconSprites.length - 1);
                this.selectedGodName = "God " + (randomGod + 1);
                this.godDescription = "Randomly selected God " + (randomGod + 1);
            }).bounds(buttonWidth / 2 - 50, startY + 3 * (buttonHeight + padding), 100, 20).build());
        // Ajouter un bouton pour la sélection aléatoire
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
