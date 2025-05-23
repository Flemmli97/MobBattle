package io.github.flemmli97.mobbattle.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.MobEffectGive;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiEffect extends Screen {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID, "textures/gui/effect.png");
    private final int xSize = 200;
    private final int ySize = 100;
    private EditBox potionBox;
    private EditBox durationBox;
    private EditBox amplifierBox;
    private ButtonCheck particleButton;

    private final Component durationTxt = new TranslatableComponent("mobbattle.gui.duration");
    private final Component amplifierTxt = new TranslatableComponent("mobbattle.gui.amplifier");
    private final Component particleTxt = new TranslatableComponent("mobbattle.gui.particle");

    private String potion;
    private int duration, amplifier;
    private boolean particle;

    public GuiEffect() {
        super(new TranslatableComponent("mobbattle.gui.potion"));
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
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potionBox = new SuggestionEditBox(this.font, i + 30, j + 21, 140, 14, TextComponent.EMPTY, 5, false, Registry.MOB_EFFECT.keySet());
        this.potionBox.setResponder(s -> {
            this.potion = s;
            try {
                ResourceLocation id = new ResourceLocation(s);
                if (Registry.MOB_EFFECT.containsKey(id)) {
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

        this.durationBox = new EditBox(this.font, i + 30, j + 62, 52, 10, TextComponent.EMPTY) {
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

        this.amplifierBox = new EditBox(this.font, i + 108, j + 62, 23, 10, TextComponent.EMPTY) {

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
            GuiEffect.this.particle = ((ButtonCheck) button).isChecked();
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
        if (this.potionBox.canConsumeInput() && this.potionBox.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
        this.font.draw(matrix, this.getTitle(), this.potionBox.x, j + 10, 1);
        int y = j + 62 - 14;
        this.font.draw(matrix, this.durationTxt, this.durationBox.x, y, 1);
        float txtX = this.amplifierBox.x + this.amplifierBox.getWidth() * 0.5f;
        float partLen = this.font.width(this.amplifierTxt) * 0.5f;
        this.font.draw(matrix, this.amplifierTxt, (txtX - partLen), y, 1);
        txtX = this.particleButton.x + this.particleButton.getWidth() * 0.5f;
        partLen = this.font.width(this.particleTxt) * 0.5f;
        this.font.draw(matrix, this.particleTxt, (txtX - partLen), y, 1);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }
}
