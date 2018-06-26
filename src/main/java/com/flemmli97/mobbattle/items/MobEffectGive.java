package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobEffectGive extends ItemSword{
	
	public MobEffectGive()
	{
		super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_effect_give");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_effect_give"));	
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.NONE;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add(TextFormatting.AQUA + "Right click to edit potion effect");
		list.add(TextFormatting.AQUA + "Left click an entity to add the potion effects");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(hand==EnumHand.MAIN_HAND)
			player.openGui(MobBattle.instance, 1, world, (int)player.posX, (int)player.posY, (int)player.posZ);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));	
		}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(entity instanceof EntityLivingBase)
		{
			EntityLiving e = (EntityLiving) entity;
			if(stack.hasTagCompound())
			{
				NBTTagCompound compound = stack.getTagCompound();
				String potionString = compound.getString(MobBattle.MODID+":potion");
				int duration = compound.getInteger(MobBattle.MODID+":duration");
				int amplifier = compound.getInteger(MobBattle.MODID+":amplifier");
				boolean showEffect = compound.getBoolean(MobBattle.MODID+":show");
				Potion potion = Potion.getPotionFromResourceLocation(potionString);
				if(potion!=null)
				{
					e.addPotionEffect(new PotionEffect(potion, duration, amplifier, false, showEffect));
					if (!player.world.isRemote)
					{
						player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added effect " + potionString + " "+amplifier + " for " + duration + " ticks "));
					}
				}
			}
		}
	    return true;
	}
	
	 @SideOnly(Side.CLIENT)
	    public void initModel() {
	        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	    }

}
