package com.flemmli97.mobbattle.items.entitymanager;

import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class EntityAIItemPickup extends EntityAIBase
{
    private EntityLiving entity;
    private List<EntityItem> nearby;
    public EntityAIItemPickup(EntityLiving creature)
    {
        this.setMutexBits(1);
        this.entity=creature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        if(this.entity.getAttackTarget()==null)
        {
            List<EntityItem> list = this.entity.worldObj.<EntityItem>getEntitiesWithinAABB(EntityItem.class, this.entity.getEntityBoundingBox().expandXyz(8));
            if (list.isEmpty())
            {
                return false;
            }
            else
            {
            	this.nearby=list;
                return true;
            }
        }
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        EntityItem item = this.nearby.get(this.entity.getRNG().nextInt(this.nearby.size()));
        this.entity.getNavigator().tryMoveToXYZ(item.posX, item.posY, item.posZ, 1);
    }

	@Override
	public void updateTask() {
		for (EntityItem entityitem : this.entity.worldObj.getEntitiesWithinAABB(EntityItem.class, this.entity.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)))
        {
            if (!entityitem.isDead && entityitem.getEntityItem()!=null && !entityitem.cannotPickup())
            {
                this.updateEquipmentIfNeeded(entityitem);
            }
        }
	}
	
	private boolean isItemBetter(ItemStack stack, ItemStack currentEquipped)
	{
		if(stack.getItem() instanceof ItemArmor)
		{
			if(currentEquipped==null)
				return true;
			else if(currentEquipped.getItem() instanceof ItemArmor)
			{
				 ItemArmor itemarmor = (ItemArmor)stack.getItem();
	             ItemArmor itemarmor1 = (ItemArmor)currentEquipped.getItem();

	             if (itemarmor.damageReduceAmount == itemarmor1.damageReduceAmount)
	             {
	                 return stack.getMetadata() > currentEquipped.getMetadata() || stack.hasTagCompound() && !currentEquipped.hasTagCompound();
	             }
	             else
	             {
	                 return itemarmor.damageReduceAmount > itemarmor1.damageReduceAmount;
	             }
			}
		}
		else if(stack.getItem() instanceof ItemBow)
		{
			if(currentEquipped==null)
				return true;
			if(currentEquipped.getItem() instanceof ItemBow)
			{
				int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
				int power2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, currentEquipped);
				return power>power2;
			}
		}
		else
		{
			if(currentEquipped==null)
				return true;
			ModifiableAttributeInstance m = new ModifiableAttributeInstance(new AttributeMap(), SharedMonsterAttributes.ATTACK_DAMAGE);
			for(AttributeModifier a : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()))
				m.applyModifier(a);
			double dmg=m.getAttributeValue();
			int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
			if(sharp>0)
				dmg+=sharp*0.5+0.5;
			
			ModifiableAttributeInstance mEquip = new ModifiableAttributeInstance(new AttributeMap(), SharedMonsterAttributes.ATTACK_DAMAGE);
			for(AttributeModifier a : currentEquipped.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()))
				mEquip.applyModifier(a);
			double dmgEquip=mEquip.getAttributeValue();
			int sharpEquip = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, currentEquipped);
			if(sharpEquip>0)
				dmgEquip+=sharpEquip*0.5+0.5;
			return dmg>dmgEquip;
		}
		return false;
	}
    
	protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
        ItemStack itemstack = itemEntity.getEntityItem().copy();
        itemstack.stackSize=1;
        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = this.entity.getItemStackFromSlot(entityequipmentslot);
        if (this.isItemBetter(itemstack, itemstack1))
        {
        	if(itemstack1!=null)
        	this.entity.entityDropItem(itemstack1, 0.0F);
            this.entity.setItemStackToSlot(entityequipmentslot, itemstack);
            this.entity.setDropChance(entityequipmentslot, 0);
            this.entity.enablePersistence();
            this.entity.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 1.4F + 2.0F);
            itemEntity.getEntityItem().stackSize--;
        }
    }
}