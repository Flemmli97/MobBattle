package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiArmor extends AbstractContainerScreen<ContainerArmor> {

    private final Component chatComponent;
    private static final ResourceLocation armorGui = new ResourceLocation(MobBattle.MODID, "textures/gui/armor.png");

    public GuiArmor(ContainerArmor container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.chatComponent = title;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.chatComponent, this.imageWidth / 2 - this.font.width(this.chatComponent) / 2, 6, 4210752, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(armorGui, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
