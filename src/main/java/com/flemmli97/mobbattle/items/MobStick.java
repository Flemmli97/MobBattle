package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.items.entitymanager.Team;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MobStick extends Item{
		
	public MobStick()
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_stick"));
	}
	
	@Override
	public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) 
	{
	      return !player.isCreative();
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {

		if(stack.hasTag() && stack.getTag().hasKey("StoredEntityName"))
		{
			list.add(new TextComponentString(TextFormatting.GREEN + "Asigned entity: " + stack.getTag().getString("StoredEntityName")));
		}
		list.add(new TextComponentString(TextFormatting.AQUA + "Left click to asign an entity"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Right click to reset"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!player.world.isRemote)
			if(stack.hasTag())
			{
				stack.getTag().removeTag("StoredEntity");
				stack.getTag().removeTag("StoredEntityName");
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset entities"));
			}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(!player.world.isRemote)
			if (stack.hasTag() && stack.getTag().hasKey("StoredEntity"))
			{
				EntityLiving storedEntity = Team.fromUUID(player.world, stack.getTag().getString("StoredEntity"));
				if (entity instanceof EntityLiving && entity != storedEntity)
				{		
					EntityLiving living = (EntityLiving) entity;
					living.setAttackTarget(storedEntity);
					storedEntity.setAttackTarget(living);
					stack.getTag().removeTag("StoredEntity");
					stack.getTag().removeTag("StoredEntityName");
					return true;
				}			
			}
			else if (entity instanceof EntityLiving)
			{
				NBTTagCompound compound = new NBTTagCompound();
				if(stack.hasTag())
					compound = stack.getTag();
				compound.setString("StoredEntity", entity.getCachedUniqueIdString());
				compound.setString("StoredEntityName", entity.getClass().getSimpleName());
				stack.setTag(compound);
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "First entity set, hit another entity to set target"));
				return true;
			}
	    return true;
	}
}
