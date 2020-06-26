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

    //drawGuiContainerForegroundLayer
    @Override
    protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY) {
        String s = this.chatComponent.getUnformattedComponentText();
        //drawString
        this.field_230712_o_.func_238405_a_(matrix, s, this.xSize / 2 - this.field_230712_o_.getStringWidth(s) / 2, 6, 4210752);
    }

    //render
    @Override
    public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
        //renderHoveredTooltip
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    //drawGuiContainerBackgroundLayer
    @Override
    protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.field_230706_i_.getTextureManager().bindTexture(armorGui);
        //width
        int i = (this.field_230708_k_ - this.xSize) / 2;
        //height
        int j = (this.field_230709_l_ - this.ySize) / 2;
        //blit
        this.func_238474_b_(matrix, i, j, 0, 0, this.xSize, this.ySize);
    }
}
