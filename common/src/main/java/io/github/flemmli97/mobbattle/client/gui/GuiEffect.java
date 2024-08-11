package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiEffect extends Screen {

    private static final ResourceLocation TEX = MobBattle.of("textures/gui/effect.png");
    private final int xSize = 176;
    private final int ySize = 80;
    private EditBox potion;
    private EditBox duration;
    private EditBox amplifier;
    private ButtonCheck button;
    private EffectComponent effect;

    public GuiEffect() {
        super(Component.translatable("mobbattle.gui.potions"));
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
        this.effect = stack.getOrDefault(CrossPlatformStuff.INSTANCE.getComponentEffect(), EffectComponent.DEFAULT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new EditBox(this.font, i + 29, j + 20, 110, 16, Component.empty()) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (super.charTyped(typedChar, keyCode)) {
                    BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(this.getValue()))
                            .ifPresent(eff -> GuiEffect.this.effect = GuiEffect.this.effect.withEffect(eff));
                    return true;
                }
                return false;
            }
        };
        this.potion.setMaxLength(35);
        this.potion.setEditable(true);
        this.potion.setValue(this.effect.effect().map(Holder::getRegisteredName).orElse(""));
        this.addWidget(this.potion);

        this.duration = new EditBox(this.font, i + 17, j + 48, 36, 12, Component.empty()) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            GuiEffect.this.effect = GuiEffect.this.effect.withDuration(Integer.parseInt(this.getValue()));
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.duration.setMaxLength(6);
        this.duration.setEditable(true);
        this.duration.setValue(this.effect.duration() > 0 ? "" + this.effect.duration() : "");
        this.addWidget(this.duration);

        this.amplifier = new EditBox(this.font, i + 71, j + 48, 26, 12, Component.empty()) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            int i = Integer.parseInt(this.getValue());
                            if (i > 255)
                                this.setValue("" + 255);
                            GuiEffect.this.effect = GuiEffect.this.effect.withAmplifier(Integer.parseInt(this.getValue()));
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.amplifier.setMaxLength(3);
        this.amplifier.setEditable(true);
        this.amplifier.setValue(this.effect.amplifier() > 0 ? "" + this.effect.amplifier() : "");
        this.addWidget(this.amplifier);

        this.button = new ButtonCheck(i + 140, j + 49, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            GuiEffect.this.effect = GuiEffect.this.effect.withParticles(((ButtonCheck) button).isChecked());
        });
        this.addRenderableWidget(this.button);
        this.button.checkUncheck(this.effect.particles());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        boolean texFocused = this.potion.isFocused() || this.amplifier.isFocused() || this.duration.isFocused();
        if ((keyCode == 256 && this.shouldCloseOnEsc()) || (!texFocused && ClientPlatform.INSTANCE.keyMatches(this.minecraft.options.keyInventory, keyCode, scanCode))) {
            if (!this.effect.equals(EffectComponent.DEFAULT))
                ClientPlatform.INSTANCE.itemStackUpdatePacket(this.effect);
            this.onClose();
            return true;
        } else
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.potion.mouseClicked(mouseX, mouseY, mouseButton);
        this.duration.mouseClicked(mouseX, mouseY, mouseButton);
        this.amplifier.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        graphics.blit(TEX, i, j, 0, 0, this.xSize, this.ySize);
        this.potion.render(graphics, mouseX, mouseY, partialTicks);
        this.duration.render(graphics, mouseX, mouseY, partialTicks);
        this.amplifier.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(this.font, "Potion:", i + 30, j + 10, 1, false);
        graphics.drawString(this.font, "Duration:", i + 18, j + 39, 1, false);
        graphics.drawString(this.font, "Amplifier:", i + 70, j + 39, 1, false);
        graphics.drawString(this.font, "Particle:", i + 130, j + 39, 1, false);
    }
}
