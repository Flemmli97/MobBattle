package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonCheck extends Button {

    private static final ResourceLocation SPRITE = MobBattle.of("widget/checkbox");
    private static final ResourceLocation SPRITE_CHECK = MobBattle.of("widget/checkbox_ticked");
    private boolean check;

    public ButtonCheck(int x, int y, OnPress press) {
        super(x, y, 12, 12, Component.empty(), press, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blitSprite(RenderType::guiTextured, this.check ? SPRITE_CHECK : SPRITE, this.getX(), this.getY(), this.width, this.height);
    }

    public void checkUncheck(boolean check) {
        this.check = check;
    }

    public boolean isChecked() {
        return this.check;
    }
}
