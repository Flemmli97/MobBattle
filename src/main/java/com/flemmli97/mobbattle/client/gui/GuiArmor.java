package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.inv.ContainerArmor;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiArmor extends ContainerScreen<ContainerArmor>{

	private final ITextComponent chatComponent;
    private static final ResourceLocation armorGui = new ResourceLocation(MobBattle.MODID, "textures/gui/armor.png");

	public GuiArmor(int windowID, PlayerInventory playerInv, MobEntity living) {
		super(new ContainerArmor(windowID, playerInv, living), playerInv, living.getName());
		chatComponent = living.getDisplayName();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.chatComponent.getUnformattedComponentText();
        this.font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6, 4210752);
    }
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(armorGui);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
	}
}
