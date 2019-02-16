package com.flemmli97.mobbattle.client.gui;

import com.flemmli97.mobbattle.ItemStackUpdate;
import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.PacketHandler;

import net.minecraft.client.Minecraft;
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
    private ItemStack stack=ItemStack.EMPTY;
	public GuiEffect() {
		this.stack=Minecraft.getInstance().player.getHeldItemMainhand();
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
        this.mc.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.potion = new GuiTextField(0, this.fontRenderer, i+30, j + 21, 108, 14) {
        	@Override
			public boolean charTyped(char typedChar, int keyCode) {
        		if (super.charTyped(typedChar, keyCode))
                {
                	NBTTagCompound compound = GuiEffect.this.stack.hasTag()?GuiEffect.this.stack.getTag():new NBTTagCompound();;
            		compound.setString(MobBattle.MODID+":potion", this.getText());
            		GuiEffect.this.stack.setTag(compound);
            		return true;
                }
        		return false;
        	}
        };
        this.potion.setMaxStringLength(35);
        this.potion.setEnabled(true);
        this.potion.setText(this.stack.hasTag()?this.stack.getTag().getString(MobBattle.MODID+":potion"):"");
        this.children.add(this.potion);
        
        this.duration = new GuiTextField(0, this.fontRenderer, i+18, j + 49, 34, 10) {
			@Override
			public boolean charTyped(char typedChar, int keyCode) {
				if(Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode))
				{
					NBTTagCompound compound = GuiEffect.this.stack.hasTag()?GuiEffect.this.stack.getTag():new NBTTagCompound();;
		        	if(super.charTyped(typedChar, keyCode) && !this.getText().isEmpty())
			        {
		        		try
		        		{
			        		compound.setInt(MobBattle.MODID+":duration", Integer.parseInt(this.getText()));
			        		GuiEffect.this.stack.setTag(compound);
		        		}
		        		catch(NumberFormatException e)
		        		{
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
        this.duration.setText(this.stack.hasTag()?""+this.stack.getTag().getInt(MobBattle.MODID+":duration"):"");
        this.children.add(this.duration);
        
        this.amplifier = new GuiTextField(0, this.fontRenderer, i+70, j + 49, 28, 10) {
			@Override
			public boolean charTyped(char typedChar, int keyCode) {
				if(Character.isDigit(typedChar) || GuiEffect.this.isHelperKey(keyCode))
				{
					NBTTagCompound compound = GuiEffect.this.stack.hasTag()?GuiEffect.this.stack.getTag():new NBTTagCompound();;
		        	if(super.charTyped(typedChar, keyCode) && !this.getText().isEmpty())
			        {
		        		try
		        		{
			        		int i = Integer.parseInt(this.getText());
			        		if(i>255)
			        			this.setText(""+255);
			        		compound.setInt(MobBattle.MODID+":amplifier", Integer.parseInt(this.getText()));
			        		GuiEffect.this.stack.setTag(compound);
		        		}
		        		catch(NumberFormatException e)
		        		{
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
        this.amplifier.setText(this.stack.hasTag()?""+this.stack.getTag().getInt(MobBattle.MODID+":amplifier"):"");
        this.children.add(this.amplifier);
        
        this.button= new ButtonCheck(0,i+140,j+49) {
        	@Override
			public void onClick(double mouseX, double mouseY) {
        		this.checkUncheck(!this.isChecked());
            	NBTTagCompound compound = GuiEffect.this.stack.hasTag()?GuiEffect.this.stack.getTag():new NBTTagCompound();
        		compound.setBoolean(MobBattle.MODID+":show", ((ButtonCheck) button).isChecked());
        		GuiEffect.this.stack.setTag(compound);
             }
        };
        this.addButton(this.button);
        this.button.checkUncheck(this.stack.hasTag()?this.stack.getTag().getBoolean(MobBattle.MODID+":show"):true);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256 && this.allowCloseWithEscape()) 
        {
        	if(this.stack.hasTag())
        		PacketHandler.sendToServer(new ItemStackUpdate(this.stack.getTag()));
        	this.close();
        	return true;
        } 
        else 
           return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
     }
    
    private boolean isHelperKey(int keyCode)
    {
    	return keyCode==14 || keyCode==199|| keyCode== 203|| keyCode== 205|| keyCode== 207|| keyCode== 211;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        this.potion.mouseClicked(mouseX, mouseY, mouseButton);
        this.duration.mouseClicked(mouseX, mouseY, mouseButton);
        this.amplifier.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
    {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(tex);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.potion.drawTextField(mouseX, mouseY, partialTicks);
        this.duration.drawTextField(mouseX, mouseY, partialTicks);
        this.amplifier.drawTextField(mouseX, mouseY, partialTicks);
        this.fontRenderer.drawString("Potion:", i+30, j+10, 1);
        this.fontRenderer.drawString("Duration:", i+18, j+39, 1);
        this.fontRenderer.drawString("Amplifier:", i+70, j+39, 1);
        this.fontRenderer.drawString("Particle:", i+130, j+39, 1);

        super.render(mouseX, mouseY, partialTicks);
	}
}
