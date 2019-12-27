package com.flemmli97.mobbattle.inv;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.network.EquipMessage;
import com.flemmli97.mobbattle.network.PacketHandler;

import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryArmor extends Inventory {

    private final MobEntity theEntity;

    public InventoryArmor(MobEntity living) {
        super(6);
        this.theEntity = living;
        for(int x = 0; x < 6; x++){
            ItemStack stack = living.getItemStackFromSlot(MobBattle.slot[x]);
            this.updateSlotContents(x, stack);
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        PacketHandler.sendToServer(new EquipMessage(stack, theEntity.getEntityId(), index));
        super.setInventorySlotContents(index, stack);
    }

    public void updateSlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
}
