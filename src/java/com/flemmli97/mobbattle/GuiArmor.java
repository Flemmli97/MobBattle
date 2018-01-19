package com.flemmli97.mobbattle;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiArmor extends GuiContainer{

	private final ITextComponent chatComponent;
    private static final ResourceLocation armorGui = new ResourceLocation(MobBattle.MODID + ":textures/gui/armor.png");

	public GuiArmor(InventoryPlayer playerInv, EntityLiving living) {
		super(new ContainerArmor(playerInv, living));
		chatComponent = living.getDisplayName();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.chatComponent.getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(armorGui);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
