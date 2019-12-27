package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.network.ItemStackUpdate;
import com.flemmli97.mobbattle.network.PacketHandler;
import com.mojang.blaze3d.platform.GlStateManager;

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
    private ItemStack stack = ItemStack.EMPTY;

    public GuiEffect() {
        super(new StringTextComponent("Potions"));
        this.stack = Minecraft.getInstance().player.getHeldItemMainhand();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new TextFieldWidget(this.font, i + 30, j + 21, 108, 14, "") {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if(super.charTyped(typedChar, keyCode)){
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    ;
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
        this.children.add(this.potion);

        this.duration = new TextFieldWidget(this.font, i + 18, j + 49, 34, 10, "") {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if(Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)){
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    ;
                    if(super.charTyped(typedChar, keyCode) && !this.getText().isEmpty()){
                        try{
                            compound.putInt(MobBattle.MODID + ":duration", Integer.parseInt(this.getText()));
                            GuiEffect.this.stack.setTag(compound);
                        }catch(NumberFormatException e){
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
        this.children.add(this.duration);

        this.amplifier = new TextFieldWidget(this.font, i + 70, j + 49, 28, 10, "") {

            @Override
            public boolean charTyped(char typedChar, int keyCode) {
                if(Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode)){
                    CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                    ;
                    if(super.charTyped(typedChar, keyCode) && !this.getText().isEmpty()){
                        try{
                            int i = Integer.parseInt(this.getText());
                            if(i > 255)
                                this.setText("" + 255);
                            compound.putInt(MobBattle.MODID + ":amplifier", Integer.parseInt(this.getText()));
                            GuiEffect.this.stack.setTag(compound);
                        }catch(NumberFormatException e){
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
        this.children.add(this.amplifier);

        this.button = new ButtonCheck(i + 140, j + 49) {

            @Override
            public void onClick(double mouseX, double mouseY) {
                this.checkUncheck(!this.isChecked());
                CompoundNBT compound = GuiEffect.this.stack.hasTag() ? GuiEffect.this.stack.getTag() : new CompoundNBT();
                compound.putBoolean(MobBattle.MODID + ":show", ((ButtonCheck) button).isChecked());
                GuiEffect.this.stack.setTag(compound);
            }
        };
        this.addButton(this.button);
        this.button.checkUncheck(this.stack.hasTag() ? this.stack.getTag().getBoolean(MobBattle.MODID + ":show") : false);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if(p_keyPressed_1_ == 256 && this.shouldCloseOnEsc()){
            if(this.stack.hasTag())
                PacketHandler.sendToServer(new ItemStackUpdate(this.stack.getTag()));
            this.onClose();
            return true;
        }else
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        this.potion.render(mouseX, mouseY, partialTicks);
        this.duration.render(mouseX, mouseY, partialTicks);
        this.amplifier.render(mouseX, mouseY, partialTicks);
        this.font.drawString("Potion:", i + 30, j + 10, 1);
        this.font.drawString("Duration:", i + 18, j + 39, 1);
        this.font.drawString("Amplifier:", i + 70, j + 39, 1);
        this.font.drawString("Particle:", i + 130, j + 39, 1);

        super.render(mouseX, mouseY, partialTicks);
    }
}
