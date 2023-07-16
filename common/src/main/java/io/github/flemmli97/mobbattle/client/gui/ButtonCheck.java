package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonCheck extends Button {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID, "textures/gui/effect.png");
    private boolean check;

    public ButtonCheck(int x, int y, OnPress press) {
        super(x, y, 10, 10, Component.empty(), press, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(tex, this.getX(), this.getY(), 178, this.check ? 14 : 1, this.width, this.height);
    }

    public void checkUncheck(boolean check) {
        this.check = check;
    }

    public boolean isChecked() {
        return this.check;
    }
}
