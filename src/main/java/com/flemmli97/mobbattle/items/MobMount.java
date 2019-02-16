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

public class MobMount extends Item{

	public MobMount() 
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_mount"));
    }
	
	@Override
	public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) 
	{
		return !player.isCreative();
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) 
	{
		list.add(new TextComponentString(TextFormatting.AQUA + "Left click an entity to select"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Left click another entity to add selected entity as rider"));
	}
		
	@Override
	public boolean hasEffect(ItemStack stack) 
	{
		return stack.hasTag() && stack.getTag().hasKey("StoredEntity");
	}
	 
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!player.world.isRemote)
			if(stack.hasTag())
			{
				stack.getTag().removeTag("StoredEntity");
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset entities"));
			}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) 
	{
		if(entity instanceof EntityLiving && !player.world.isRemote)
		{
			if (stack.hasTag() && stack.getTag().hasKey("StoredEntity"))
			{
				EntityLiving storedEntity = Team.fromUUID(player.world, stack.getTag().getString("StoredEntity"));
				if(storedEntity!=null && storedEntity!=entity && !this.passengerContainsEntity(storedEntity, entity))
				{
					storedEntity.startRiding(entity);
					stack.getTag().removeTag("StoredEntity");
				}
			}
			else
			{
				NBTTagCompound compound = new NBTTagCompound();
				if(stack.hasTag())
					compound = stack.getTag();
				compound.setString("StoredEntity", entity.getCachedUniqueIdString());
				stack.setTag(compound);
			}
		}
		return true;
	}
	
	private boolean passengerContainsEntity(Entity theEntity, Entity entitySearch)
	{
		if(!theEntity.getPassengers().isEmpty())
			if(theEntity.getPassengers().contains(entitySearch))
				return true;
			else
				return this.passengerContainsEntity(theEntity.getPassengers().get(0), entitySearch);
		return false;
	}
}
