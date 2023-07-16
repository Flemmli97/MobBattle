package io.github.flemmli97.mobbattle.handler;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.EnumSet;
import java.util.List;

public class EntityAIItemPickup extends Goal {

    private final Mob entity;
    private List<ItemEntity> nearby;

    public EntityAIItemPickup(Mob creature) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.entity = creature;
    }

    /**
     * Returns whether the Goal should begin execution.
     */
    @Override
    public boolean canUse() {
        if (this.entity.getTarget() == null) {
            List<ItemEntity> list = this.entity.level().getEntitiesOfClass(ItemEntity.class, this.entity.getBoundingBox().inflate(8));
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
    public void start() {
        ItemEntity item = this.nearby.get(this.entity.getRandom().nextInt(this.nearby.size()));
        this.entity.getNavigation().moveTo(item.getX(), item.getY(), item.getZ(), 1);
    }

    @Override
    public void tick() {
        for (ItemEntity entityitem : this.entity.level().getEntitiesOfClass(ItemEntity.class, this.entity.getBoundingBox().inflate(1.0D, 0.0D, 1.0D))) {
            if (!entityitem.getItem().isEmpty() && !entityitem.hasPickUpDelay()) {
                this.updateEquipmentIfNeeded(entityitem);
            }
        }
    }

    private boolean isItemBetter(ItemStack stack, ItemStack currentEquipped) {
        if (stack.getItem() instanceof ArmorItem) {
            if (!(currentEquipped.getItem() instanceof ArmorItem) || EnchantmentHelper.hasBindingCurse(currentEquipped))
                return true;
            else if (currentEquipped.getItem() instanceof ArmorItem itemarmor1) {
                ArmorItem itemarmor = (ArmorItem) stack.getItem();

                if (itemarmor.getDefense() == itemarmor1.getDefense()) {
                    return stack.getDamageValue() > currentEquipped.getDamageValue() || stack.hasTag() && !currentEquipped.hasTag();
                } else {
                    return itemarmor.getDefense() > itemarmor1.getDefense();
                }
            }
        } else if (stack.getItem() instanceof BowItem) {
            if (currentEquipped.isEmpty())
                return true;
            if (currentEquipped.getItem() instanceof BowItem) {
                int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                int power2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, currentEquipped);
                return power > power2;
            }
        } else {
            if (currentEquipped.isEmpty())
                return true;
            AttributeInstance m = new AttributeInstance(Attributes.ATTACK_DAMAGE, (inst) -> {
            });
            for (AttributeModifier a : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE))
                m.addPermanentModifier(a);
            double dmg = m.getValue();
            int sharp = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, stack);
            if (sharp > 0)
                dmg += sharp * 0.5 + 0.5;

            AttributeInstance mEquip = new AttributeInstance(Attributes.ATTACK_DAMAGE, (inst) -> {
            });
            for (AttributeModifier a : currentEquipped.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE))
                mEquip.addPermanentModifier(a);
            double dmgEquip = mEquip.getValue();
            int sharpEquip = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, currentEquipped);
            if (sharpEquip > 0)
                dmgEquip += sharpEquip * 0.5 + 0.5;
            return dmg > dmgEquip;
        }
        return false;
    }

    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem().copy();
        itemstack.setCount(1);
        EquipmentSlot EquipmentSlotType = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = this.entity.getItemBySlot(EquipmentSlotType);
        if (this.isItemBetter(itemstack, itemstack1)) {
            this.entity.spawnAtLocation(itemstack1, 0.0F);
            this.entity.setItemSlot(EquipmentSlotType, itemstack);
            this.entity.setDropChance(EquipmentSlotType, 0);
            this.entity.setPersistenceRequired();
            this.entity.level().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    (this.entity.getRandom().nextFloat() - this.entity.getRandom().nextFloat()) * 1.4F + 2.0F);
            itemEntity.getItem().shrink(1);
        }
    }
}
