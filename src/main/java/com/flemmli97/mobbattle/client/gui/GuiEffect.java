package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.network.ItemStackUpdate;
import com.flemmli97.mobbattle.network.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiEffect extends Screen {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID + ":textures/gui/effect.png");
    private int xSize = 176;
    private int ySize = 80;
    private TextFieldWidget potion;
    private TextFieldWidget duration;
    private TextFieldWidget amplifier;
    private ButtonCheck button;
    private ItemStack stack;

    public GuiEffect() {
        super(new StringTextComponent("Potions"));
        this.stack = Minecraft.getInstance().player.getHeldItemMainhand();
    }

    //isPauseScreen
    @Override
    public boolean func_231177_au__() {
        return false;
    }

    //init
    @Override
    public void func_231023_e_() {
        super.func_231023_e_();
        //mc
        this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
        //width
        int i = (this.field_230708_k_ - this.xSize) / 2;
        //height
        int j = (this.field_230709_l_ - this.ySize) / 2;
        this.potion = new TextFieldWidget(this.field_230712_o_, i + 30, j + 21, 108, 14, StringTextComponent.field_240750_d_) {
            //charTyped
            @Override
            public boolean func_231042_a_(char typedChar, int keyCode) {
                if (super.func_231042_a_(typedChar, keyCode)) {
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
        this.field_230710_m_.add(this.potion);

        this.duration = new TextFieldWidget(this.field_230712_o_, i + 18, j + 49, 34, 10, StringTextComponent.field_240750_d_) {

            @Override
            public boolean func_231042_a_(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    if (super.func_231042_a_(typedChar, keyCode) && !this.getText().isEmpty()) {
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
        this.field_230710_m_.add(this.duration);

        this.amplifier = new TextFieldWidget(this.field_230712_o_, i + 70, j + 49, 28, 10, StringTextComponent.field_240750_d_) {

            @Override
            public boolean func_231042_a_(char typedChar, int keyCode) {
                if (Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)) {
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    if (super.func_231042_a_(typedChar, keyCode) && !this.getText().isEmpty()) {
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
        this.field_230710_m_.add(this.amplifier);

        this.button = new ButtonCheck(i + 140, j + 49, (button) -> {
            ButtonCheck check = (ButtonCheck) button;
            check.checkUncheck(!check.isChecked());
            CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
            compound.putBoolean(MobBattle.MODID + ":show", ((ButtonCheck) button).isChecked());
            GuiEffect.this.stack.setTag(compound);
        });
        //AddWidget
        this.func_230480_a_(this.button);
        this.button.checkUncheck(this.stack.hasTag() && this.stack.getTag().getBoolean(MobBattle.MODID + ":show"));
    }

    //keyPressed
    @Override
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        //shouldCloseOnEsc
        if (p_keyPressed_1_ == 256 && this.func_231178_ax__()) {
            if (this.stack.hasTag())
                PacketHandler.sendToServer(new ItemStackUpdate(this.stack.getTag()));
            //onclose
            this.func_231175_as__();
            return true;
        } else
            return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    //mouseClicked
    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
        this.potion.func_231044_a_(mouseX, mouseY, mouseButton);
        this.duration.func_231044_a_(mouseX, mouseY, mouseButton);
        this.amplifier.func_231044_a_(mouseX, mouseY, mouseButton);
        return super.func_231044_a_(mouseX, mouseY, mouseButton);
    }

    //render
    @Override
    public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.field_230706_i_.getTextureManager().bindTexture(tex);
        int i = (this.field_230708_k_ - this.xSize) / 2;
        int j = (this.field_230709_l_ - this.ySize) / 2;
        this.func_238474_b_(matrix, i, j, 0, 0, this.xSize, this.ySize);
        this.potion.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
        this.duration.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
        this.amplifier.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
        this.field_230712_o_.func_238405_a_(matrix, "Potion:", i + 30, j + 10, 1);
        this.field_230712_o_.func_238405_a_(matrix, "Duration:", i + 18, j + 39, 1);
        this.field_230712_o_.func_238405_a_(matrix, "Amplifier:", i + 70, j + 39, 1);
        this.field_230712_o_.func_238405_a_(matrix, "Particle:", i + 130, j + 39, 1);

        super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
    }
}
