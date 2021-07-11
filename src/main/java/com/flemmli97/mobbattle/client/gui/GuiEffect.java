package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.network.ItemStackUpdate;
import com.flemmli97.mobbattle.network.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiEffect extends Screen {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID + ":textures/gui/effect.png");
    private final int xSize = 176;
    private final int ySize = 80;
    private TextFieldWidget potion;
    private TextFieldWidget duration;
    private TextFieldWidget amplifier;
    private ButtonCheck button;
    private final ItemStack stack;

    public GuiEffect() {
        super(new StringTextComponent("Potions"));
        this.stack = Minecraft.getInstance().player.getHeldItemMainhand();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new TextFieldWidget(this.font, i + 30, j + 21, 108, 14, StringTextComponent.EMPTY) {
            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (super.charTyped(typedChar, keyCode)) {
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    compound.putString(MobBattle.MODID + ":potion", this.getText());
                    GuiEffect.this.stack.setTag(compound);
                    return true;
                }
                return false;
            }
        };
        this.potion.setMaxStringLength(35);
        this.potion.setEnabled(true);
        this.potion.setText(this.stack.hasTag() ? this.stack.getTag().getString(MobBattle.MODID + ":potion") : "");
        this.addButton(this.potion);

        this.duration = new TextFieldWidget(this.font, i + 18, j + 49, 34, 10, StringTextComponent.EMPTY) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    if (super.charTyped(typedChar, keyCode) && !this.getText().isEmpty()) {
                        try {
                            compound.putInt(MobBattle.MODID + ":duration", Integer.parseInt(this.getText()));
                            GuiEffect.this.stack.setTag(compound);
                        } catch (NumberFormatException e) {
                            MobBattle.logger.error(this.getText() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.duration.setMaxStringLength(6);
        this.duration.setEnabled(true);
        this.duration.setText(this.stack.hasTag() ? "" + this.stack.getTag().getInt(MobBattle.MODID + ":duration") : "");
        this.addButton(this.duration);

        this.amplifier = new TextFieldWidget(this.font, i + 70, j + 49, 28, 10, StringTextComponent.EMPTY) {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    if (super.charTyped(typedChar, keyCode) && !this.getText().isEmpty()) {
                        try {
                            int i = Integer.parseInt(this.getText());
                            if (i > 255)
                                this.setText("" + 255);
                            compound.putInt(MobBattle.MODID + ":amplifier", Integer.parseInt(this.getText()));
                            GuiEffect.this.stack.setTag(compound);
                        } catch (NumberFormatException e) {
                            MobBattle.logger.error(this.getText() + " not a number");
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        this.amplifier.setMaxStringLength(3);
        this.amplifier.setEnabled(true);
        this.amplifier.setText(this.stack.hasTag() ? "" + this.stack.getTag().getInt(MobBattle.MODID + ":amplifier") : "");
        this.addButton(this.amplifier);

        this.button = new ButtonCheck(i + 140, j + 49, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
            compound.putBoolean(MobBattle.MODID + ":show", ((ButtonCheck) button).isChecked());
            GuiEffect.this.stack.setTag(compound);
        });
        //AddWidget
        this.addButton(this.button);
        this.button.checkUncheck(this.stack.hasTag() && this.stack.getTag().getBoolean(MobBattle.MODID + ":show"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        boolean texFocused = this.potion.isFocused() || this.amplifier.isFocused() || this.duration.isFocused();
        if ((keyCode == 256 && this.shouldCloseOnEsc()) || (!texFocused && this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))) {
            if (this.stack.hasTag())
                PacketHandler.sendToServer(new ItemStackUpdate(this.stack.getTag()));
            this.closeScreen();
            return true;
        } else
            return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    //mouseClicked
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.potion.mouseClicked(mouseX, mouseY, mouseButton);
        this.duration.mouseClicked(mouseX, mouseY, mouseButton);
        this.amplifier.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //render
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.minecraft.getTextureManager().bindTexture(tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
        this.potion.render(matrix, mouseX, mouseY, partialTicks);
        this.duration.render(matrix, mouseX, mouseY, partialTicks);
        this.amplifier.render(matrix, mouseX, mouseY, partialTicks);
        this.font.drawStringWithShadow(matrix, "Potion:", i + 30, j + 10, 1);
        this.font.drawStringWithShadow(matrix, "Duration:", i + 18, j + 39, 1);
        this.font.drawStringWithShadow(matrix, "Amplifier:", i + 70, j + 39, 1);
        this.font.drawStringWithShadow(matrix, "Particle:", i + 130, j + 39, 1);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }
}
