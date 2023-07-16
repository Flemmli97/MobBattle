package io.github.flemmli97.mobbattle.inv;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class InventoryArmor extends SimpleContainer {

    private final Mob mob;

    public InventoryArmor(Mob living) {
        super(6);
        this.mob = living;
        for (int x = 0; x < 6; x++) {
            ItemStack stack = living.getItemBySlot(MobBattle.slot[x]);
            this.updateSlotContents(x, stack);
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (this.mob.level().isClientSide)
            CrossPlatformStuff.INSTANCE.sendEquipMessage(stack, this.mob.getId(), index);
        super.setItem(index, stack);
    }

    public void updateSlotContents(int index, ItemStack stack) {
        super.setItem(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
