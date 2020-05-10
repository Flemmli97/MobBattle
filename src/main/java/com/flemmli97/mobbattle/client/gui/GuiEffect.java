package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.CommonProxy;
import com.flemmli97.mobbattle.ItemStackUpdate;
import com.flemmli97.mobbattle.MobBattle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiEffect extends GuiScreen {

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID + ":textures/gui/effect.png");
    private int xSize = 176;
    private int ySize = 80;
    private GuiTextField potion;
    private GuiTextField duration;
    private GuiTextField amplifier;
    private ButtonCheck button;
    private ItemStack stack;

    public GuiEffect() {
        this.stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new GuiTextField(0, this.fontRenderer, i + 30, j + 21, 108, 14);
        this.potion.setMaxStringLength(35);
        this.potion.setEnabled(true);
        this.potion.setText(this.stack.hasTagCompound() ? this.stack.getTagCompound().getString(MobBattle.MODID + ":potion") : "");
        this.duration = new GuiTextField(0, this.fontRenderer, i + 18, j + 49, 34, 10); //y = 49
        this.duration.setMaxStringLength(6);
        this.duration.setEnabled(true);
        this.duration.setText(this.stack.hasTagCompound() ? "" + this.stack.getTagCompound().getInteger(MobBattle.MODID + ":duration") : "");

        this.amplifier = new GuiTextField(0, this.fontRenderer, i + 70, j + 49, 28, 10);
        this.amplifier.setMaxStringLength(3);
        this.amplifier.setEnabled(true);
        this.amplifier.setText(this.stack.hasTagCompound() ? "" + this.stack.getTagCompound().getInteger(MobBattle.MODID + ":amplifier") : "");
        this.button = new ButtonCheck(0, i + 140, j + 49);
        this.buttonList.add(this.button);
        this.button.checkUncheck(!this.stack.hasTagCompound() || this.stack.getTagCompound().getBoolean(MobBattle.MODID + ":show"));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(this.potion.textboxKeyTyped(typedChar, keyCode)){
            NBTTagCompound compound = this.stack.hasTagCompound() ? this.stack.getTagCompound() : new NBTTagCompound();
            compound.setString(MobBattle.MODID + ":potion", this.potion.getText());
            this.stack.setTagCompound(compound);
        }else if(Character.isDigit(typedChar) || this.isHelperKey(keyCode)){
            NBTTagCompound compound = this.stack.hasTagCompound() ? this.stack.getTagCompound() : new NBTTagCompound();
            if(this.duration.textboxKeyTyped(typedChar, keyCode) && !this.duration.getText().isEmpty()){
                try{
                    compound.setInteger(MobBattle.MODID + ":duration", Integer.parseInt(this.duration.getText()));
                    this.stack.setTagCompound(compound);
                }catch(NumberFormatException e){
                    MobBattle.logger.error(this.duration.getText() + " not a number");
                }
            }else if(this.amplifier.textboxKeyTyped(typedChar, keyCode) && !this.amplifier.getText().isEmpty()){
                try{
                    int i = Integer.parseInt(this.amplifier.getText());
                    if(i > 255)
                        this.amplifier.setText("" + 255);
                    compound.setInteger(MobBattle.MODID + ":amplifier", Integer.parseInt(this.amplifier.getText()));
                    this.stack.setTagCompound(compound);
                }catch(NumberFormatException e){
                    MobBattle.logger.error(this.duration.getText() + " not a number");
                }
            }
        }else if(keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)){
            if(this.stack.hasTagCompound())
                CommonProxy.sendToServer(new ItemStackUpdate(this.stack.getTagCompound()));
            this.mc.player.closeScreen();
        }
    }

    private boolean isHelperKey(int keyCode) {
        return keyCode == 14 || keyCode == 199 || keyCode == 203 || keyCode == 205 || keyCode == 207 || keyCode == 211;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.potion.mouseClicked(mouseX, mouseY, mouseButton);
        this.duration.mouseClicked(mouseX, mouseY, mouseButton);
        this.amplifier.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button == this.button){
            ((ButtonCheck) button).checkUncheck(!((ButtonCheck) button).isChecked());
            NBTTagCompound compound = this.stack.hasTagCompound() ? this.stack.getTagCompound() : new NBTTagCompound();
            compound.setBoolean(MobBattle.MODID + ":show", ((ButtonCheck) button).isChecked());
            this.stack.setTagCompound(compound);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.potion.drawTextBox();
        this.duration.drawTextBox();
        this.amplifier.drawTextBox();
        this.fontRenderer.drawString("Potion:", i + 30, j + 10, 1);
        this.fontRenderer.drawString("Duration:", i + 18, j + 39, 1);
        this.fontRenderer.drawString("Amplifier:", i + 70, j + 39, 1);
        this.fontRenderer.drawString("Particle:", i + 130, j + 39, 1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
