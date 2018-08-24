package com.flemmli97.mobbattle.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

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

public class GuiEffect extends GuiScreen{

    private static final ResourceLocation tex = new ResourceLocation(MobBattle.MODID + ":textures/gui/effect.png");
	private int xSize=176;
	private int ySize=80;
    private GuiTextField potion;
    private GuiTextField duration;
    private GuiTextField amplifier;
    private ButtonCheck button;
    private ItemStack stack;
	public GuiEffect() {
		this.stack=Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
	}
	@Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    @Override
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new GuiTextField(0, this.fontRendererObj, i+30, j + 21, 108, 14);
        this.potion.setMaxStringLength(35);
        this.potion.setEnabled(true);
        if(stack!=null)
        this.potion.setText(stack.hasTagCompound()?stack.getTagCompound().getString(MobBattle.MODID+":potion"):"");
        this.duration = new GuiTextField(0, this.fontRendererObj, i+18, j + 49, 34, 10); //y = 49
        this.duration.setMaxStringLength(6);
        this.duration.setEnabled(true);
        if(stack!=null)
        this.duration.setText(stack.hasTagCompound()?""+stack.getTagCompound().getInteger(MobBattle.MODID+":duration"):"");

        this.amplifier = new GuiTextField(0, this.fontRendererObj, i+70, j + 49, 28, 10);
        this.amplifier.setMaxStringLength(3);
        this.amplifier.setEnabled(true);
        if(stack!=null)
        this.amplifier.setText(stack.hasTagCompound()?""+stack.getTagCompound().getInteger(MobBattle.MODID+":amplifier"):"");
        this.button= new ButtonCheck(0,i+140,j+49);
        this.buttonList.add(this.button);
        if(stack!=null)
        this.button.checkUncheck(stack.hasTagCompound()?stack.getTagCompound().getBoolean(MobBattle.MODID+":show"):true);
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.potion.textboxKeyTyped(typedChar, keyCode) && stack!=null)
        {
        	NBTTagCompound compound = stack.hasTagCompound()?stack.getTagCompound():new NBTTagCompound();;
    		compound.setString(MobBattle.MODID+":potion", this.potion.getText());
    		stack.setTagCompound(compound);
        }
        else if((Character.isDigit(typedChar) || this.isHelperKey(keyCode)) && stack!=null)
        {
        	NBTTagCompound compound = stack.hasTagCompound()?stack.getTagCompound():new NBTTagCompound();;
        	if(this.duration.textboxKeyTyped(typedChar, keyCode) && !this.duration.getText().isEmpty())
	        {
        		try
        		{
	        		compound.setInteger(MobBattle.MODID+":duration", Integer.parseInt(this.duration.getText()));
	        		stack.setTagCompound(compound);
        		}
        		catch(NumberFormatException e)
        		{
        			MobBattle.logger.error(this.duration.getText() + " not a number");
        		}
	        }
	        else if(this.amplifier.textboxKeyTyped(typedChar, keyCode) && !this.amplifier.getText().isEmpty())
	        {
	        	try
        		{
	        		int i = Integer.parseInt(this.amplifier.getText());
	        		if(i>255)
	        			this.amplifier.setText(""+255);
	        		compound.setInteger(MobBattle.MODID+":amplifier", Integer.parseInt(this.amplifier.getText()));
	        		stack.setTagCompound(compound);
        		}
        		catch(NumberFormatException e)
        		{
        			MobBattle.logger.error(this.duration.getText() + " not a number");
        		}
	        }
        }
        else if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
        {
        	if(stack.hasTagCompound())
        		CommonProxy.sendToServer(new ItemStackUpdate(stack.getTagCompound()));
            this.mc.thePlayer.closeScreen();
        }
    }
    
    private boolean isHelperKey(int keyCode)
    {
    	return keyCode==14 || keyCode==199|| keyCode== 203|| keyCode== 205|| keyCode== 207|| keyCode== 211;
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.potion.mouseClicked(mouseX, mouseY, mouseButton);
        this.duration.mouseClicked(mouseX, mouseY, mouseButton);
        this.amplifier.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button == this.button && stack!=null)
		{
			((ButtonCheck) button).checkUncheck(!((ButtonCheck) button).isChecked());
        	NBTTagCompound compound = stack.hasTagCompound()?stack.getTagCompound():new NBTTagCompound();
    		compound.setBoolean(MobBattle.MODID+":show", ((ButtonCheck) button).isChecked());
    		stack.setTagCompound(compound);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.potion.drawTextBox();
        this.duration.drawTextBox();
        this.amplifier.drawTextBox();
        this.fontRendererObj.drawString("Potion:", i+30, j+10, 1);
        this.fontRendererObj.drawString("Duration:", i+18, j+39, 1);
        this.fontRendererObj.drawString("Amplifier:", i+70, j+39, 1);
        this.fontRendererObj.drawString("Particle:", i+130, j+39, 1);

        super.drawScreen(mouseX, mouseY, partialTicks);
	}
}