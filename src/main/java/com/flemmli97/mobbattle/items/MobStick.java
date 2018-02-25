package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.entityManager.Team;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
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

		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("StoredEntityName"))
		{
			list.add(TextFormatting.GREEN + "Asigned entity: " + stack.getTagCompound().getString("StoredEntityName"));
		}
		list.add(TextFormatting.AQUA + "Left click to asign an entity");
		list.add(TextFormatting.AQUA + "Right click to reset");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!player.world.isRemote)
		if(stack.hasTagCompound())
		{
			stack.getTagCompound().removeTag("StoredEntity");
			stack.getTagCompound().removeTag("StoredEntityName");
			player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset entities"));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(!player.world.isRemote)
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("StoredEntity"))
			{
				EntityLiving storedEntity = Team.fromUUID(player.world, stack.getTagCompound().getString("StoredEntity"));
				if (entity instanceof EntityLiving && entity != storedEntity)
				{		
					EntityLiving living = (EntityLiving) entity;
					living.setAttackTarget(storedEntity);
					storedEntity.setAttackTarget(living);
					stack.getTagCompound().removeTag("StoredEntity");
					stack.getTagCompound().removeTag("StoredEntityName");
					return true;
				}			
			}
			else if (entity instanceof EntityLiving)
			{
				NBTTagCompound compound = new NBTTagCompound();
				if(stack.hasTagCompound())
					compound = stack.getTagCompound();
				compound.setString("StoredEntity", entity.getCachedUniqueIdString());
				compound.setString("StoredEntityName", entity.getClass().getSimpleName());
				stack.setTagCompound(compound);
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "First entity set, hit another entity to set target"));
				return true;
			}
	    return true;
	}
	
	 @SideOnly(Side.CLIENT)
	    public void initModel() {
	        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	    }
	
}
