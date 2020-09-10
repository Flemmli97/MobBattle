package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.inv.ContainerArmor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiArmor extends ContainerScreen<ContainerArmor> {

    private final ITextComponent chatComponent;
    private static final ResourceLocation armorGui = new ResourceLocation(MobBattle.MODID, "textures/gui/armor.png");

    public GuiArmor(int windowID, PlayerInventory playerInv, MobEntity living) {
        super(new ContainerArmor(windowID, playerInv, living), playerInv, living.getName());
        chatComponent = living.getDisplayName();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY) {
        String s = this.chatComponent.getUnformattedComponentText();
        this.font.drawString(matrix, s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6, 4210752);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(armorGui);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
    }
}
