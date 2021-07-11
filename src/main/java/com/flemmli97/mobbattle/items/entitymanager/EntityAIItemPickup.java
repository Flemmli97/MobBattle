package com.flemmli97.mobbattle.items.entitymanager;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import java.util.EnumSet;
import java.util.List;

public class EntityAIItemPickup extends Goal {

    private final MobEntity entity;
    private List<ItemEntity> nearby;

    public EntityAIItemPickup(MobEntity creature) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.entity = creature;
    }

    /**
     * Returns whether the Goal should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (this.entity.getAttackTarget() == null) {
            List<ItemEntity> list = this.entity.world.getEntitiesWithinAABB(ItemEntity.class, this.entity.getBoundingBox().grow(8));
            if (list.isEmpty()) {
                return false;
            } else {
                this.nearby = list;
                return true;
            }
        }
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        ItemEntity item = this.nearby.get(this.entity.getRNG().nextInt(this.nearby.size()));
        this.entity.getNavigator().tryMoveToXYZ(item.getPosX(), item.getPosY(), item.getPosZ(), 1);
    }

    @Override
    public void tick() {
        for (ItemEntity entityitem : this.entity.world.getEntitiesWithinAABB(ItemEntity.class, this.entity.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
            if (!entityitem.getItem().isEmpty() && !entityitem.cannotPickup()) {
                this.updateEquipmentIfNeeded(entityitem);
            }
        }
    }

    private boolean isItemBetter(ItemStack stack, ItemStack currentEquipped) {
        if (stack.getItem() instanceof ArmorItem) {
            if (!(currentEquipped.getItem() instanceof ArmorItem) || EnchantmentHelper.hasBindingCurse(currentEquipped))
                return true;
            else if (currentEquipped.getItem() instanceof ArmorItem) {
                ArmorItem itemarmor = (ArmorItem) stack.getItem();
                ArmorItem itemarmor1 = (ArmorItem) currentEquipped.getItem();

                if (itemarmor.getDamageReduceAmount() == itemarmor1.getDamageReduceAmount()) {
                    return stack.getDamage() > currentEquipped.getDamage() || stack.hasTag() && !currentEquipped.hasTag();
                } else {
                    return itemarmor.getDamageReduceAmount() > itemarmor1.getDamageReduceAmount();
                }
            }
        } else if (stack.getItem() instanceof BowItem) {
            if (currentEquipped.isEmpty())
                return true;
            if (currentEquipped.getItem() instanceof BowItem) {
                int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                int power2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, currentEquipped);
                return power > power2;
            }
        } else {
            if (currentEquipped.isEmpty())
                return true;
            ModifiableAttributeInstance m = new ModifiableAttributeInstance(Attributes.ATTACK_DAMAGE, (inst) -> {
            });
            for (AttributeModifier a : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE))
                m.applyPersistentModifier(a);
            double dmg = m.getValue();
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
            if (sharp > 0)
                dmg += sharp * 0.5 + 0.5;

            ModifiableAttributeInstance mEquip = new ModifiableAttributeInstance(Attributes.ATTACK_DAMAGE, (inst) -> {
            });
            for (AttributeModifier a : currentEquipped.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE))
                mEquip.applyPersistentModifier(a);
            double dmgEquip = mEquip.getValue();
            int sharpEquip = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, currentEquipped);
            if (sharpEquip > 0)
                dmgEquip += sharpEquip * 0.5 + 0.5;
            return dmg > dmgEquip;
        }
        return false;
    }

    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem().copy();
        itemstack.setCount(1);
        EquipmentSlotType EquipmentSlotType = MobEntity.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = this.entity.getItemStackFromSlot(EquipmentSlotType);
        if (this.isItemBetter(itemstack, itemstack1)) {
            this.entity.entityDropItem(itemstack1, 0.0F);
            this.entity.setItemStackToSlot(EquipmentSlotType, itemstack);
            this.entity.setDropChance(EquipmentSlotType, 0);
            this.entity.enablePersistence();
            this.entity.world.playSound(null, this.entity.getPosX(), this.entity.getPosY(), this.entity.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                    (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 1.4F + 2.0F);
            itemEntity.getItem().shrink(1);
        }
    }
}
