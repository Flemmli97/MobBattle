package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobStick extends ItemSword{
	
	EntityLiving storedEntity = null;
	
	public MobStick()
	{
		super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_stick");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        GameRegistry.register(this, new ResourceLocation(MobBattle.MODID, "mob_stick"));

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
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {

		if(storedEntity != null)
		{
			list.add(TextFormatting.GREEN + "Asigned entity: " + storedEntity.getClass().getSimpleName());
		}
		list.add(TextFormatting.AQUA + "Left click to asign an entity");
		list.add(TextFormatting.AQUA + "Right click to reset");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(!player.worldObj.isRemote)
		if(storedEntity != null)
		{
			storedEntity = null;
			if (!player.worldObj.isRemote)
			{
				player.addChatMessage(new TextComponentString(TextFormatting.RED + "Reset entities"));
			}	
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(!player.worldObj.isRemote)
		if (storedEntity != null)
		{
			if (entity instanceof EntityLiving && entity != storedEntity)
			{		
				EntityLiving living = (EntityLiving) entity;
				living.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) storedEntity), 0.0F);
				living.setAttackTarget(storedEntity);
				storedEntity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity), 0.0F);
				storedEntity.setAttackTarget(living);
				storedEntity = null;
				return true;
			}			
		}
		else if (storedEntity == null && entity instanceof EntityLiving)
		{
			storedEntity = (EntityLiving) entity;
			if (!player.worldObj.isRemote)
			{
				player.addChatMessage(new TextComponentString(TextFormatting.GOLD + "First entity set, hit another entity to set target"));
			}
			return true;
		}
	    return true;
	}
	
	 @SideOnly(Side.CLIENT)
	    public void initModel() {
	        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	    }
	
}
