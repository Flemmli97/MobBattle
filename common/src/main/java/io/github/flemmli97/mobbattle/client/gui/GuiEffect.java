package io.github.flemmli97.mobbattle.client.gui;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.MobEffectGive;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiEffect extends Screen {

    private static final ResourceLocation TEX = new ResourceLocation(MobBattle.MODID, "textures/gui/effect.png");
    private final int xSize = 200;
    private final int ySize = 100;
    private EditBox potionBox;
    private EditBox durationBox;
    private EditBox amplifierBox;
    private ButtonCheck particleButton;

    private final Component durationTxt = Component.translatable("mobbattle.gui.duration");
    private final Component amplifierTxt = Component.translatable("mobbattle.gui.amplifier");
    private final Component particleTxt = Component.translatable("mobbattle.gui.particle");

    private String potion;
    private int duration, amplifier;
    private boolean particle;

    public GuiEffect() {
        super(Component.translatable("mobbattle.gui.potions"));
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
        if (stack.getItem() instanceof MobEffectGive give) {
            MobEffectGive.EffectData data = give.getData(stack);
            this.potion = data.potion();
            this.duration = data.duration();
            this.amplifier = data.amplifier();
            this.particle = data.particle();
        }
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
        this.potionBox = new SuggestionEditBox(this.font, i + 30, j + 21, 140, 14, Component.empty(), 5, false,
                SuggestionEditBox.ofResourceLocation(BuiltInRegistries.MOB_EFFECT.keySet()));
        this.potionBox.setResponder(s -> {
            this.potion = s;
            try {
                ResourceLocation id = new ResourceLocation(s);
                if (BuiltInRegistries.MOB_EFFECT.containsKey(id)) {
                    this.potionBox.setTextColor(0xE0E0E0);
                } else {
                    this.potionBox.setTextColor(0xFF0000);
                }
            } catch (Exception e) {
                this.potionBox.setTextColor(0xFF0000);
            }
        });
        this.potionBox.setMaxLength(35);
        this.potionBox.setEditable(true);
        this.potionBox.setValue(this.potion);

        this.durationBox = new EditBox(this.font, i + 30, j + 62, 52, 10, Component.empty()) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            GuiEffect.this.duration = Integer.parseInt(this.getValue());
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
        this.durationBox.setValue(this.duration + "");
        this.addRenderableWidget(this.durationBox);

        this.amplifierBox = new EditBox(this.font, i + 108, j + 62, 26, 10, Component.empty()) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            int i = Integer.parseInt(this.getValue());
                            if (i > 255)
                                this.setValue("" + 255);
                            GuiEffect.this.amplifier = i;
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
        this.amplifierBox.setValue(this.amplifier + "");
        this.addRenderableWidget(this.amplifierBox);

        this.particleButton = new ButtonCheck(i + 160, j + 62, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            GuiEffect.this.particle = check.isChecked();
        });
        this.addRenderableWidget(this.particleButton);
        this.particleButton.checkUncheck(this.particle);
        this.addRenderableWidget(this.potionBox);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        boolean texFocused = this.potionBox.isFocused() || this.amplifierBox.isFocused() || this.durationBox.isFocused();
        if ((keyCode == 256 && this.shouldCloseOnEsc()) || (!texFocused && ClientPlatform.INSTANCE.keyMatches(this.minecraft.options.keyInventory, keyCode, scanCode))) {
            CrossPlatformStuff.INSTANCE.sendToServer(new C2SEffectStack(this.potion, this.duration, this.amplifier, this.particle));
            this.onClose();
            return true;
        } else
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.potionBox.canConsumeInput() && this.potionBox.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean click = super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!click)
            this.setFocused(null);
        return click;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
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
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
