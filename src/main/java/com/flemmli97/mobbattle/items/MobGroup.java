package com.flemmli97.mobbattle.items;

import java.util.ArrayList;
import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.items.entitymanager.Team;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MobGroup extends Item{
		
	public MobGroup()
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_group"));
	}
	
	@Override
	public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) 
	{
		return !player.isCreative();
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<ITextComponent> list, ITooltipFlag b) {
		list.add(new TextComponentString(TextFormatting.AQUA + "Left click to select entities"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Right click on entity to set the target"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Shift right click to reset"));
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
			EntityLivingBase entity, EnumHand hand) {
		if(!player.isSneaking() && !player.world.isRemote && stack.hasTag() && stack.getTag().hasKey("EntityList"))
		{			
			NBTTagList list = stack.getTag().getList("EntityList", 8);
			for(int i = 0; i < list.size(); i ++)
			{
				EntityLiving e = Team.fromUUID(player.world, list.getString(i));
				if (entity != e && e!=null)
				{			
					EntityLiving living = (EntityLiving) entity;
					living.setAttackTarget(e);
					e.setAttackTarget(living);
				}
			}
			stack.getTag().removeTag("EntityList");
			player.setHeldItem(hand, stack);
		}
		return true;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!player.world.isRemote && stack.hasTag() && stack.getTag().hasKey("EntityList"))
		{
			if(!player.isSneaking() && stack.getTag().getList("EntityList", 8).size()>0)
			{
				NBTTagList list = stack.getTag().getList("EntityList", 8);
				list.removeTag(list.size()-1);
				stack.getTag().setTag("EntityList", list);
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Removed an entity"));
			}
			else
			{
				stack.getTag().removeTag("EntityList");
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset all entities"));
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityLiving && !player.world.isRemote)
		{		
			NBTTagCompound compound = new NBTTagCompound();
			if(stack.hasTag())
				compound = stack.getTag();
			ArrayList<String> list = new ArrayList<String>();

			if(compound.hasKey("EntityList"))
			{
				for(int i = 0; i <compound.getList("EntityList", 8).size();i++)
				{
					list.add(compound.getList("EntityList", 8).getString(i));
				}
			}
			if(!list.contains(entity.getCachedUniqueIdString()))
			{
	            NBTTagList nbttaglist = new NBTTagList();
	            if(compound.hasKey("EntityList"))
	            		nbttaglist = compound.getList("EntityList", 8);
				nbttaglist.add(new NBTTagString(entity.getCachedUniqueIdString()));
				compound.setTag("EntityList", nbttaglist);
				stack.setTag(compound);
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added an entity"));
			}
		}
	    return true;
	}
}
