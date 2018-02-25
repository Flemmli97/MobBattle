package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.entityManager.EntityAITeamTarget;
import com.flemmli97.mobbattle.items.entityManager.Team;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobArmy extends ItemSword{
		
	public MobArmy()
	{
		super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_army");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        GameRegistry.register(this, new ResourceLocation(MobBattle.MODID, "mob_army"));
        this.setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
	    for (int i = 0; i < 4; i ++) {
	        list.add(new ItemStack(item, 1, i));
	    }
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
		switch(stack.getMetadata())
		{
			case 0:
				list.add(TextFormatting.AQUA + "Right click block to set first, and then second corner of the box");
				list.add(TextFormatting.AQUA + "Right click into air to to add entities in the box to " + TextFormatting.DARK_BLUE + "BLUE" + " team");
				list.add(TextFormatting.AQUA + "Shift-Right click to reset box");
				break;
			case 1:
				list.add(TextFormatting.AQUA + "Left click to add entities to " + TextFormatting.DARK_BLUE + "BLUE" + " team");

				break;
			case 2:
				list.add(TextFormatting.AQUA + "Right click block to set first, and then second corner of the box");
				list.add(TextFormatting.AQUA + "Right click into air to to add entities in the box to " + TextFormatting.DARK_RED + "RED" + " team");
				list.add(TextFormatting.AQUA + "Shift-Right click to reset box");
				break;
			case 3:
				list.add(TextFormatting.AQUA + "Left click to add entities to " + TextFormatting.DARK_RED + "RED" + " team");
				break;
			default:
				break;
		}
	}
	
	public BlockPos[] getSelPos(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
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
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(stack.getMetadata()==0 || stack.getMetadata()==2)
		{
			NBTTagCompound compound = stack.getTagCompound();
			if(compound==null)
				compound = new NBTTagCompound();
			if(!compound.hasKey("Position1") || compound.getIntArray("Position1")==null)
			{
				compound.setIntArray("Position1", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			}
			else if(!pos.equals(new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2])))
			{
				compound.setIntArray("Position2", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			}
			stack.setTagCompound(compound);
	        return EnumActionResult.SUCCESS;
		}
        return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(stack.getMetadata()==0 || stack.getMetadata()==2)
		{
			if(player.isSneaking() && stack.hasTagCompound())
			{
				stack.getTagCompound().removeTag("Position1");
				stack.getTagCompound().removeTag("Position2");
				if(!player.world.isRemote)
					player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset Positions"));
			}
			else if(stack.getTagCompound().hasKey("Position1") && stack.getTagCompound().hasKey("Position2"))
			{
				BlockPos pos1 = new BlockPos(stack.getTagCompound().getIntArray("Position1")[0],stack.getTagCompound().getIntArray("Position1")[1],stack.getTagCompound().getIntArray("Position1")[2]);
				BlockPos pos2 = new BlockPos(stack.getTagCompound().getIntArray("Position2")[0],stack.getTagCompound().getIntArray("Position2")[1],stack.getTagCompound().getIntArray("Position2")[2]);
				AxisAlignedBB bb = Team.getBoundingBoxPositions(pos1, pos2);
				List<EntityCreature> list = player.world.getEntitiesWithinAABB(EntityCreature.class, bb);
				for(EntityCreature living : list)
				{
					if(!player.world.isRemote)
					{
						Team.addEntityToTeam(living, this.getTeamMeta(stack));
						living.targetTasks.addTask(1, new EntityAITeamTarget(living, false, true));
					}
				}
				if(!player.world.isRemote)
				{
					player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added entities in the box to team " + this.getTeamMeta(stack)));
				}
				stack.getTagCompound().removeTag("Position1");
				stack.getTagCompound().removeTag("Position2");
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityCreature && !player.world.isRemote)
		{		
			if(stack.getMetadata()==1 || stack.getMetadata()==3)
			{			
				Team.addEntityToTeam(entity, this.getTeamMeta(stack));
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added entity to team " + this.getTeamMeta(stack)));
				((EntityCreature)entity).targetTasks.addTask(1, new EntityAITeamTarget((EntityCreature) entity, false, true));
			}
		}
	    return true;
	}
	
	private String getTeamMeta(ItemStack stack)
	{
		if(stack.getMetadata()==0 || stack.getMetadata()==1)
			return "BLUE";
		return "RED";
	}
	
	 @SideOnly(Side.CLIENT)
	    public void initModel() {
		 for(int i = 0; i < 4; i++)
	        ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName()+"_"+i, "inventory"));
	    }
}
