package io.github.flemmli97.mobbattle.common.inv;

import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryArmor extends SimpleContainer {

    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final Mob mob;

    public InventoryArmor(Mob living) {
        super(6);
        this.mob = living;
        for (int x = 0; x < 6; x++) {
            ItemStack stack = living.getItemBySlot(SLOTS[x]);
            super.setItem(x, stack);
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
        EquipmentSlot slot = this.slotType(index);
        if (slot != null && !this.mob.level().isClientSide())
            this.mob.setItemSlot(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        EquipmentSlot slot = this.slotType(index);
        if (slot == null)
            return false;
        return switch (slot) {
            case CHEST, LEGS, FEET -> CrossPlatformStuff.INSTANCE.canEquip(stack, SLOTS[index], this.mob);
            default -> true;
        };
    }

    @Nullable
    public EquipmentSlot slotType(int index) {
        if (index < 0 || index >= SLOTS.length)
            return null;
        return SLOTS[index];
    }

    public Mob getMob() {
        return this.mob;
    }
}
