package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.gui.widget.ButtonCheck;
import io.github.flemmli97.mobbattle.client.gui.widget.SuggestionEditBox;
import io.github.flemmli97.mobbattle.common.components.EffectComponent;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class EffectScreen extends Screen {

    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(MobBattle.MODID, "textures/gui/effect.png");
    private final int xSize = 200;
    private final int ySize = 100;
    private EditBox potionBox;
    private EditBox durationBox;
    private EditBox amplifierBox;
    private ButtonCheck particleButton;

    private final Component durationTxt = Component.translatable("mobbattle.gui.duration");
    private final Component amplifierTxt = Component.translatable("mobbattle.gui.amplifier");
    private final Component particleTxt = Component.translatable("mobbattle.gui.particle");

    private EffectComponent effect;

    public EffectScreen() {
        super(Component.translatable("mobbattle.gui.potions"));
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
        this.effect = stack.getOrDefault(MobBattleDataComponents.EFFECT.get(), EffectComponent.DEFAULT);
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
        this.potionBox = new SuggestionEditBox(this.font, i + 29, j + 20, 142, 16, Component.empty(), 5, false,
                SuggestionEditBox.ofResourceLocation(BuiltInRegistries.MOB_EFFECT.keySet()));
        this.potionBox.setResponder(s -> {
            try {
                Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(s));
                if (effect.isPresent()) {
                    this.potionBox.setTextColor(0xE0E0E0);
                    EffectScreen.this.effect = EffectScreen.this.effect.withEffect(effect.get());
                } else {
                    this.potionBox.setTextColor(0xFF0000);
                }
            } catch (Exception e) {
                this.potionBox.setTextColor(0xFF0000);
            }
        });
        this.potionBox.setMaxLength(35);
        this.potionBox.setEditable(true);
        this.potionBox.setValue(this.effect.effect().map(Holder::getRegisteredName).orElse(""));
        this.addRenderableWidget(this.potionBox);

        this.durationBox = new EditBox(this.font, i + 29, j + 61, 54, 12, Component.empty()) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || EffectScreen.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            EffectScreen.this.effect = EffectScreen.this.effect.withDuration(Integer.parseInt(this.getValue()));
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.durationBox.setMaxLength(7);
        this.durationBox.setEditable(true);
        this.durationBox.setValue(this.effect.duration() > 0 ? "" + this.effect.duration() : "");
        this.addRenderableWidget(this.durationBox);

        this.amplifierBox = new EditBox(this.font, i + 107, j + 61, 28, 12, Component.empty()) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || EffectScreen.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            int i = Integer.parseInt(this.getValue());
                            if (i > 255)
                                this.setValue("" + 255);
                            EffectScreen.this.effect = EffectScreen.this.effect.withAmplifier(Integer.parseInt(this.getValue()));
                        } catch (NumberFormatException e) {
                            MobBattle.LOGGER.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.amplifierBox.setMaxLength(3);
        this.amplifierBox.setEditable(true);
        this.amplifierBox.setValue(this.effect.amplifier() > 0 ? "" + this.effect.amplifier() : "");
        this.addRenderableWidget(this.amplifierBox);

        this.particleButton = new ButtonCheck(i + 160, j + 62, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            EffectScreen.this.effect = EffectScreen.this.effect.withParticles(((ButtonCheck) button).isChecked());
        });
        this.addRenderableWidget(this.particleButton);
        this.particleButton.checkUncheck(this.effect.particles());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        boolean texFocused = this.potionBox.isFocused() || this.amplifierBox.isFocused() || this.durationBox.isFocused();
        if ((keyCode == 256 && this.shouldCloseOnEsc()) || (!texFocused && ClientPlatform.INSTANCE.keyMatches(this.minecraft.options.keyInventory, keyCode, scanCode))) {
            if (!this.effect.equals(EffectComponent.DEFAULT))
                CrossPlatformStuff.INSTANCE.sendToServer(new C2SEffectStack(this.effect));
            this.onClose();
            return true;
        } else
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.potionBox.canConsumeInput() && this.potionBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean click = super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!click)
            this.setFocused(null);
        return click;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        graphics.blit(TEX, i, j, 0, 0, this.xSize, this.ySize);
        graphics.drawString(this.font, this.getTitle(), this.potionBox.getX(), j + 10, 1, false);
        int y = j + 62 - 14;
        graphics.drawString(this.font, this.durationTxt, this.durationBox.getX(), y, 1, false);
        float txtX = this.amplifierBox.getX() + this.amplifierBox.getWidth() * 0.5f;
        float partLen = this.font.width(this.amplifierTxt) * 0.5f;
        graphics.drawString(this.font, this.amplifierTxt, (int) (txtX - partLen), y, 1, false);
        txtX = this.particleButton.getX() + this.particleButton.getWidth() * 0.5f;
        partLen = this.font.width(this.particleTxt) * 0.5f;
        graphics.drawString(this.font, this.particleTxt, (int) (txtX - partLen), y, 1, false);
    }
}
