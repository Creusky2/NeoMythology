package com.mrcreusky.neomythology.client.gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
// import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpriteWidget {

    // Chemin de la texture pour ton sprite
    private final ResourceLocation spriteTexture;

    // Coordonnées et dimensions du bouton
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private OnPress onPress;

    // Constructeur du SpriteWidget
    public SpriteWidget(int x, int y, int width, int height, int spriteWidth, int spriteHeight, ResourceLocation spriteTexture, Button.OnPress onPress) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.spriteTexture = spriteTexture;
        this.onPress = onPress;
    }

    // Méthode pour construire le widget final (Button)
    public Button build() {
        // Utilise le Builder pour construire un bouton personnalisé
        Button.Builder builder = new Button.Builder(Component.literal(""), this.onPress);
        builder.bounds(this.x, this.y, this.width, this.height);

        return builder.build(buttonBuilder -> new Button(buttonBuilder) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

                // Appel de la méthode parent pour gérer l'interaction
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
                guiGraphics.blit(spriteTexture, x, y, 0, 0, width, height, width, height);
            }
        });
    }
}