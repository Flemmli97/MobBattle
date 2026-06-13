package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.inv.ContainerArmor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ArmorScreen extends AbstractContainerScreen<ContainerArmor> {

    private final Component chatComponent;
    private static final Identifier ARMOR_GUI = MobBattle.of("textures/gui/armor.png");

    public ArmorScreen(ContainerArmor container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.chatComponent = title;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(this.font, this.chatComponent, this.imageWidth / 2 - this.font.width(this.chatComponent) / 2, 6, 0xFF404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, ARMOR_GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
