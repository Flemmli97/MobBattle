package io.github.flemmli97.mobbattle.common.entity.goal;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ItemPickupGoal extends Goal {

    private final Mob entity;
    private List<ItemEntity> nearby;

    public ItemPickupGoal(Mob creature) {
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
        LivingEntity target = this.entity.getTarget() != null ? this.entity.getTarget() : this.entity;
        if (stack.has(DataComponents.EQUIPPABLE)) {
            ItemAttributeModifiers atts = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            ItemAttributeModifiers currentAtts = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (atts == null || EnchantmentHelper.has(currentEquipped, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))
                return false;
            if (currentAtts == null)
                return true;
            double[] armorAtts = new double[]{0, 0, 0, 0};
            atts.modifiers().stream().filter(e -> e.attribute().is(Attributes.ARMOR) || e.attribute().is(Attributes.ARMOR_TOUGHNESS))
                    .sorted(Comparator.comparingInt(e -> e.modifier().operation().ordinal()))
                    .forEach(e -> {
                        int idx = e.attribute().is(Attributes.ARMOR) ? 0 : 1;
                        armorAtts[idx] += switch (e.modifier().operation()) {
                            case ADD_VALUE, ADD_MULTIPLIED_BASE -> e.modifier().amount();
                            case ADD_MULTIPLIED_TOTAL -> e.modifier().amount() * armorAtts[idx];
                        };
                    });
            currentAtts.modifiers().stream().filter(e -> e.attribute().is(Attributes.ARMOR) || e.attribute().is(Attributes.ARMOR_TOUGHNESS))
                    .sorted(Comparator.comparingInt(e -> e.modifier().operation().ordinal()))
                    .forEach(e -> {
                        int idx = e.attribute().is(Attributes.ARMOR) ? 2 : 3;
                        armorAtts[idx] += switch (e.modifier().operation()) {
                            case ADD_VALUE, ADD_MULTIPLIED_BASE -> e.modifier().amount();
                            case ADD_MULTIPLIED_TOTAL -> e.modifier().amount() * armorAtts[idx];
                        };
                    });
            if (armorAtts[0] > armorAtts[2])
                return true;
            else if (armorAtts[0] < armorAtts[2])
                return false;
            if (armorAtts[1] > armorAtts[3])
                return true;
            Set<Object2IntMap.Entry<Holder<Enchantment>>> ench = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet();
            Set<Object2IntMap.Entry<Holder<Enchantment>>> currentEnch = currentEquipped.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet();
            return (armorAtts[1] == armorAtts[3] && ench.size() > currentEnch.size());
        }
        if (currentEquipped.isEmpty())
            return true;
        DamageSource damageSource1 = this.entity.damageSources().mobAttack(this.entity);
        DamageSource damageSource2 = damageSource1;
        if (stack.getItem() instanceof BowItem)
            damageSource1 = this.entity.damageSources().arrow(EntityType.ARROW.create(this.entity.level(), EntitySpawnReason.TRIGGERED), this.entity);
        if (currentEquipped.getItem() instanceof BowItem)
            damageSource2 = this.entity.damageSources().arrow(EntityType.ARROW.create(this.entity.level(), EntitySpawnReason.TRIGGERED), this.entity);
        double d1 = EnchantmentHelper.modifyDamage((ServerLevel) this.entity.level(), stack, target, damageSource1, 1);
        double d2 = EnchantmentHelper.modifyDamage((ServerLevel) this.entity.level(), currentEquipped, target, damageSource2, 1);
        return d1 > d2;
    }

    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem().copy();
        itemstack.setCount(1);
        EquipmentSlot EquipmentSlotType = this.entity.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = this.entity.getItemBySlot(EquipmentSlotType);
        if (itemEntity.level() instanceof ServerLevel serverLevel && this.isItemBetter(itemstack, itemstack1)) {
            this.entity.spawnAtLocation(serverLevel, itemstack1);
            this.entity.setItemSlot(EquipmentSlotType, itemstack);
            this.entity.setDropChance(EquipmentSlotType, 0);
            this.entity.setPersistenceRequired();
            this.entity.level().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    (this.entity.getRandom().nextFloat() - this.entity.getRandom().nextFloat()) * 1.4F + 2.0F);
            itemEntity.getItem().shrink(1);
        }
    }
}
