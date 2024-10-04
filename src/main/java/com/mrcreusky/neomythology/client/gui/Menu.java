package com.mrcreusky.neomythology.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.mrcreusky.neomythology.NeoMythology;


public class Menu extends Screen{
    // private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation BACKGROUND_TEXTURE = NeoMythology.getGuiTexture("gods_selection_menu.png");
    // ResourceLocation.isValidPath("src/main/resources/assets/neomythology/textures/gui/gods_selection_menu.png");
    
    private static final WidgetSprites godIconSprites[] = {
        new WidgetSprites(
            NeoMythology.getGuiTexture("icon_thor.png"), // Emplacement de la texture
            NeoMythology.getGuiTexture("icon_thor_hovered.png")  // Optionnel : texture lorsqu'on survole
        ),
        new WidgetSprites(
            NeoMythology.getGuiTexture("icon_odin.png"), // Emplacement de la texture
            NeoMythology.getGuiTexture("icon_odin_hovered.png")  // Optionnel : texture lorsqu'on survole
        ),
        new WidgetSprites(
            NeoMythology.getGuiTexture("gods_selection_menu.png"), // Emplacement de la texture
            NeoMythology.getGuiTexture("gods_selection_menu.png")  // Optionnel : texture lorsqu'on survole
        )
    };

    // Pour l'affichage des détails à droite
    private String selectedGodName = "Thor";  // Par défaut, on sélectionne Zeus
    private String godDescription = "Minor Gods and God Powers";  // Description par défaut
    private static final ResourceLocation GOD_DETAILS_ICON = NeoMythology.getGuiTexture( "icon_thor.png");
    public Menu() {
        super(Component.literal("NeoMythology"));
    }

    @Override
    protected void init() {
        RandomSource random = RandomSource.create();
        int buttonWidth = 64;
        int buttonHeight = 64;
        int padding = 10;
        int startX = this.width / 2 - (3 * (buttonWidth + padding)) / 2;  // Centrer la grille
        int startY = this.height / 4;

        super.init();

        // Ajouter les boutons avec icônes des dieux
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int x = startX + col * (buttonWidth + padding);
                int y = startY + row * (buttonHeight + padding);
                int index = row * 3 + col;

                if (index < godIconSprites.length) {
                    WidgetSprites godIcon = godIconSprites[index];
                    // RenderSystem.setShaderTexture(0, godIcon.get(true,false));
                    // guiGraphics.blit(50, 50, 0, 0, 64, 64);
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
            }).bounds(padding, startY + 3 * (buttonHeight + padding), 100, 20).build());

        
        // Ajouter un bouton pour la sélection aléatoire
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Dessiner l'arrière-plan
        guiGraphics.blit(BACKGROUND_TEXTURE, 0 , 0 , 0, 0, this.width, this.height, this.width, this.height);   
        // this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        // RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        // Dessiner les boutons et les composants de base

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Dessiner la partie droite (informations sur le dieu sélectionné)

        drawRightPanel(guiGraphics);

        
        ResourceLocation THOR = NeoMythology.getGuiTexture("icon_thor.png");
        TextureAtlasSprite textureAtlassprite = minecraft.getGuiSprites().getSprite(THOR);
        guiGraphics.blit(mouseX, mouseY, 0, 64, 64, textureAtlassprite);

    }

    private void drawRightPanel(GuiGraphics guiGraphics) {
        // Afficher les détails du dieu sélectionné
        int infoX = this.width / 2 + 100;
        int infoY = this.height / 4;

        // Afficher le nom du dieu
        guiGraphics.drawString(this.font, this.selectedGodName, infoX, infoY, 0xFFFFFF);
        guiGraphics.drawString(this.font, this.godDescription, infoX, infoY + 20, 0xFFFFFF);

        // Dessiner une icône (détails supplémentaires)
        // RenderSystem.setShaderTexture(0, GOD_DETAILS_ICON);
        // TextureAtlasSprite textureAtlas = (TextureAtlasSprite) Minecraft.getInstance().getTextureAtlas(GOD_DETAILS_ICON);
        // guiGraphics.blit(infoX, infoY + 40, 0, 64, 64, textureAtlas);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Indique si l'écran met le jeu en pause
    }
}
