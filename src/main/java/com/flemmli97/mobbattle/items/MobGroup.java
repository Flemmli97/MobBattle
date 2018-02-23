package com.flemmli97.mobbattle.items;

import java.util.ArrayList;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobGroup extends ItemSword{
	
	List<EntityLiving> entityList = new ArrayList<EntityLiving>();
	
	public MobGroup()
	{
		super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_group");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_group"));	
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
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		
		if(entityList.size() > 0)
		{
			list.add(TextFormatting.GREEN + "Stored " + entityList.size() + " entity");
		}
		list.add(TextFormatting.AQUA + "Left click to select entities");
		list.add(TextFormatting.AQUA + "Right click on entity to set the target");
		list.add(TextFormatting.AQUA + "Right click to remove last added entity");
		list.add(TextFormatting.AQUA + "Shift right click to reset");
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
			EntityLivingBase entity, EnumHand hand) {
		if(!player.isSneaking() && entityList != null && !player.world.isRemote)
		{			
			int size = entityList.size();
			for(int i = size; i > 0; i --)
			{
				EntityLiving e = (EntityLiving) entityList.get(i-1);
				if (entity instanceof EntityLiving && entity != e)
				{		
					EntityLiving living = (EntityLiving) entity;
					living.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) e), 0.0F);
					living.setAttackTarget(e);
					e.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity), 0.0F);
					e.setAttackTarget(living);
					entityList.remove(i-1);
				}
			}
		}
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!entityList.isEmpty() && !player.world.isRemote)
		{
			if(!player.isSneaking())
			{
				int size = entityList.size();
				entityList.remove(size-1);
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Removed an entity"));
			}
			else
			{
				entityList.clear();
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset all entities"));
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityLiving && !player.world.isRemote && !entityList.contains(entity))
		{		
			entityList.add((EntityLiving) entity);
			player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Added an entity"));
		}
	    return true;
	}
	
	
	 @SideOnly(Side.CLIENT)
	    public void initModel() {
	        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	    }
}
