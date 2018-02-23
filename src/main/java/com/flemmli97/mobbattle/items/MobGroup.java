package com.flemmli97.mobbattle.items;

import java.util.ArrayList;
import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class MobGroup extends ItemSword{
	
	List<EntityLiving> entityList = new ArrayList<EntityLiving>();
	
	public MobGroup(ToolMaterial mat)
	{
		super(mat);
        this.setUnlocalizedName("mob_group");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setTextureName(MobBattle.MODID + ":mob_group");
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.none;
	}

	@Override
	public Multimap<?, ?> getItemAttributeModifiers() {
		return HashMultimap.create();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
		
		if(entityList.size() > 0)
		{
			list.add(EnumChatFormatting.GREEN + "Stored " + entityList.size() + " entity");
		}
		list.add(EnumChatFormatting.AQUA + "Left click to select entities");
		list.add(EnumChatFormatting.AQUA + "Right click on entity to set the target");
		list.add(EnumChatFormatting.AQUA + "Right click to remove last added entity");
		list.add(EnumChatFormatting.AQUA + "Shift right click to reset");
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
			EntityLivingBase entity) {
		if(!player.isSneaking() && entityList != null && !player.worldObj.isRemote)
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!entityList.isEmpty() && !player.worldObj.isRemote)
		{
			if(!player.isSneaking())
			{
				int size = entityList.size();
				entityList.remove(size-1);
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Removed an entity"));
			}
			else
			{
				entityList.clear();
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Reset all entities"));
			}
		}
		return stack;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityLiving && !player.worldObj.isRemote && !entityList.contains(entity))
		{		
			entityList.add((EntityLiving) entity);
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Added an entity"));
		}
	    return true;
	}
}
