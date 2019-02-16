package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ButtonCheck extends GuiButton{

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID + ":textures/gui/effect.png");
	private boolean check;
	public ButtonCheck(int buttonId, int x, int y) {
		super(buttonId, x, y, 10, 10, "");
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
    {
		Minecraft.getInstance().getTextureManager().bindTexture(tex);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(this.x, this.y, 178, this.check?14:1, this.width, this.height);
    }

	public void checkUncheck(boolean check)
	{
		this.check=check;
	}
	
	public boolean isChecked()
	{
		return this.check;
	}
}
