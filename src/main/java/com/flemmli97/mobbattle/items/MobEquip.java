package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.items.entitymanager.EntityAIItemPickup;
import com.flemmli97.mobbattle.items.entitymanager.Team;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MobEquip extends Item{
		
	public MobEquip()
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_equip"));
	}
	
	@Override
	public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) 
	{
		return !player.isCreative();
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {
		list.add(new TextComponentString(TextFormatting.AQUA + "Right click block to set first, and then second corner of the box"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Right click into air to to make entities able to pickup items"));
		list.add(new TextComponentString(TextFormatting.AQUA + "Shift-Right click to reset box"));
	}
	
	public BlockPos[] getSelPos(ItemStack stack)
	{
		if(stack.hasTag())
		{
			NBTTagCompound compound = stack.getTag();
			BlockPos pos1=null;
			if(compound.hasKey("Position1") && compound.getIntArray("Position1")!=null)
				pos1 = new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2]);
			BlockPos pos2=null;
			if(compound.hasKey("Position2") && compound.getIntArray("Position2")!=null)
				pos2 = new BlockPos(compound.getIntArray("Position2")[0], compound.getIntArray("Position2")[1], compound.getIntArray("Position2")[2]);
			return new BlockPos[] {pos1, pos2};
		}
		return new BlockPos[] {null, null};
	}
	
	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx) {
		ItemStack stack = ctx.getItem();
		if(!ctx.getWorld().isRemote)
		{
			NBTTagCompound compound = stack.getTag();
			if(compound==null)
				compound = new NBTTagCompound();
			if(!compound.hasKey("Position1") || compound.getIntArray("Position1")==null)
			{
				compound.setIntArray("Position1", new int[] {ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ()});
			}
			else if(!ctx.getPos().equals(new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2])))
			{
				compound.setIntArray("Position2", new int[] {ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ()});
			}
			stack.setTag(compound);
		}
        return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote && stack.hasTag())
			if(player.isSneaking())
			{
				stack.getTag().removeTag("Position1");
				stack.getTag().removeTag("Position2");
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset Positions"));
			}
			else if(stack.getTag().hasKey("Position1") && stack.getTag().hasKey("Position2"))
			{
				BlockPos pos1 = new BlockPos(stack.getTag().getIntArray("Position1")[0],stack.getTag().getIntArray("Position1")[1],stack.getTag().getIntArray("Position1")[2]);
				BlockPos pos2 = new BlockPos(stack.getTag().getIntArray("Position2")[0],stack.getTag().getIntArray("Position2")[1],stack.getTag().getIntArray("Position2")[2]);
				AxisAlignedBB bb = Team.getBoundingBoxPositions(pos1, pos2);
				List<EntityLiving> list = player.world.getEntitiesWithinAABB(EntityLiving.class, bb);
				for(EntityLiving living : list)
				{
					living.addTag("PickUp");
					living.tasks.addTask(10, new EntityAIItemPickup(living));
				}
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Entities in box can now pickup items"));
			}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityLiving && !player.world.isRemote)
		{		
			entity.addTag("PickUp");
			((EntityLiving)entity).tasks.addTask(10, new EntityAIItemPickup((EntityLiving) entity));
			player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Entity can pickup items now"));
		}
	    return true;
	}
}
