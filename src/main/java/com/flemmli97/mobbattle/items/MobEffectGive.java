package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectGive extends Item{
	
	public MobEffectGive()
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_effect_give"));	
    }
	
	@Override
	public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) 
	{
		return !player.isCreative();
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
		list.add(new TextComponentString(TextFormatting.AQUA + "Right click to edit potion effect"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Left click an entity to add the potion effects"));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(hand==EnumHand.MAIN_HAND)
			MobBattle.proxy.openEffectGUI(player);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));	
		}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(entity instanceof EntityLivingBase && !player.world.isRemote)
		{
			EntityLivingBase e = (EntityLivingBase) entity;
			if(stack.hasTag())
			{
				NBTTagCompound compound = stack.getTag();
				String potionString = compound.getString(MobBattle.MODID+":potion");
				int duration = compound.getInt(MobBattle.MODID+":duration");
				int amplifier = compound.getInt(MobBattle.MODID+":amplifier");
				boolean showEffect = compound.getBoolean(MobBattle.MODID+":show");
				Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionString));
				if(potion!=null)
				{
					e.addPotionEffect(new PotionEffect(potion, duration, amplifier, false, showEffect));
					player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added effect " + potionString + " "+amplifier + " for " + duration + " ticks "));
				}
			}
		}
	    return true;
	}
}
