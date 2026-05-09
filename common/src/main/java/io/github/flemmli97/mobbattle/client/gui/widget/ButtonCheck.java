package io.github.flemmli97.mobbattle.client.gui.widget;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ButtonCheck extends Button {

    private static final Identifier SPRITE = MobBattle.of("widget/checkbox");
    private static final Identifier SPRITE_CHECK = MobBattle.of("widget/checkbox_ticked");
    private boolean check;

    public ButtonCheck(int x, int y, OnPress press) {
        super(x, y, 12, 12, Component.empty(), press, DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, this.check ? SPRITE_CHECK : SPRITE, this.getX(), this.getY(), this.width, this.height);
    }

    public void checkUncheck(boolean check) {
        this.check = check;
    }

    public boolean isChecked() {
        return this.check;
    }
}
