package com.mrcreusky.neomythology.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CircularSpriteWidget extends AbstractWidget {
    private final WidgetSprites sprites;
    private final Runnable onClick;
    private final String name;

    public CircularSpriteWidget(int x, int y, int width, int height, WidgetSprites sprites, Runnable onClick, String name) {
        super(x, y, width, height, Component.literal(""));
        this.sprites = sprites;
        this.onClick = onClick;
        this.name = name;
        this.setTooltip(Tooltip.create(Component.translatable(this.name)));
    }

    public String getName() {
        return this.name;
    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    // Check if hovered or focused to add special effects
        // Render the actual widget (circular)
        guiGraphics.blit(this.isHoveredOrFocused() ? sprites.enabledFocused() : sprites.enabled(), this.getX(), this.getY(), 0, 0, width, height, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.clicked(mouseX, mouseY)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick.run();
                return true;
            }
        }   
        return false;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        double centerX = this.getX() + this.width / 2.0;
        double centerY = this.getY() + this.height / 2.0;
        double radius = this.width / 2.0;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        return dx * dx + dy * dy <= radius * radius;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    // Start the narration by narrating the widget's label
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());

        // If the widget is hovered, add information that it's currently hovered
        if (this.isHovered()) {
            narrationElementOutput.add(NarratedElementType.HINT, Component.translatable("narration.cycle_sprite_widget.hovered"));
        }

        // If the widget is focused (selected), add that information as well
        if (this.isFocused()) {
            narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.cycle_sprite_widget.focused"));
        }
    }
}