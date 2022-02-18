package io.github.flemmli97.mobbattle.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.platform.ClientPlatform;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiEffect extends Screen {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID + ":textures/gui/effect.png");
    private final int xSize = 176;
    private final int ySize = 80;
    private EditBox potion;
    private EditBox duration;
    private EditBox amplifier;
    private ButtonCheck button;
    private final ItemStack stack;

    public GuiEffect() {
        super(new TextComponent("Potions"));
        this.stack = Minecraft.getInstance().player.getMainHandItem();
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
        this.potion = new EditBox(this.font, i + 30, j + 21, 108, 14, TextComponent.EMPTY) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (super.charTyped(typedChar, keyCode)) {
                    CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
                    compound.putString(MobBattle.MODID + ":potion", this.getValue());
                    GuiEffect.this.stack.setTag(compound);
                    return true;
                }
                return false;
            }
        };
        this.potion.setMaxLength(35);
        this.potion.setEditable(true);
        this.potion.setValue(this.stack.hasTag() ? this.stack.getTag().getString(MobBattle.MODID + ":potion") : "");
        this.addWidget(this.potion);

        this.duration = new EditBox(this.font, i + 18, j + 49, 34, 10, TextComponent.EMPTY) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            compound.putInt(MobBattle.MODID + ":duration", Integer.parseInt(this.getValue()));
                            GuiEffect.this.stack.setTag(compound);
                        } catch (NumberFormatException e) {
                            MobBattle.logger.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.duration.setMaxLength(6);
        this.duration.setEditable(true);
        this.duration.setValue(this.stack.hasTag() ? "" + this.stack.getTag().getInt(MobBattle.MODID + ":duration") : "");
        this.addWidget(this.duration);

        this.amplifier = new EditBox(this.font, i + 70, j + 49, 28, 10, TextComponent.EMPTY) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
                    if (super.charTyped(typedChar, keyCode) && !this.getValue().isEmpty()) {
                        try {
                            int i = Integer.parseInt(this.getValue());
                            if (i > 255)
                                this.setValue("" + 255);
                            compound.putInt(MobBattle.MODID + ":amplifier", Integer.parseInt(this.getValue()));
                            GuiEffect.this.stack.setTag(compound);
                        } catch (NumberFormatException e) {
                            MobBattle.logger.error(this.getValue() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.amplifier.setMaxLength(3);
        this.amplifier.setEditable(true);
        this.amplifier.setValue(this.stack.hasTag() ? "" + this.stack.getTag().getInt(MobBattle.MODID + ":amplifier") : "");
        this.addWidget(this.amplifier);

        this.button = new ButtonCheck(i + 140, j + 49, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            CompoundTag compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundTag();
            compound.putBoolean(MobBattle.MODID + ":show", ((ButtonCheck) button).isChecked());
            GuiEffect.this.stack.setTag(compound);
        });
        this.addRenderableWidget(this.button);
        this.button.checkUncheck(this.stack.hasTag() && this.stack.getTag().getBoolean(MobBattle.MODID + ":show"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        boolean texFocused = this.potion.isFocused() || this.amplifier.isFocused() || this.duration.isFocused();
        if ((keyCode == 256 && this.shouldCloseOnEsc()) || (!texFocused && ClientPlatform.instance().keyMatches(this.minecraft.options.keyInventory, keyCode, scanCode))) {
            if (this.stack.hasTag())
                CrossPlatformStuff.instance().itemStackUpdatePacket(this.stack.getTag());
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
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
        this.potion.render(matrix, mouseX, mouseY, partialTicks);
        this.duration.render(matrix, mouseX, mouseY, partialTicks);
        this.amplifier.render(matrix, mouseX, mouseY, partialTicks);
        this.font.draw(matrix, "Potion:", i + 30, j + 10, 1);
        this.font.draw(matrix, "Duration:", i + 18, j + 39, 1);
        this.font.draw(matrix, "Amplifier:", i + 70, j + 39, 1);
        this.font.draw(matrix, "Particle:", i + 130, j + 39, 1);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }
}
