package com.flemmli97.mobbattle.inv;

import com.flemmli97.mobbattle.MobBattle;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ContainerArmor extends Container {

    private InventoryArmor inv;

    public ContainerArmor(int windowID, PlayerInventory playerInv, MobEntity living) {
        super(MobBattle.type, windowID);
        if (living == null)
            return;
        this.inv = new InventoryArmor(living);
        this.inv.openInventory(playerInv.player);
        this.addSlot(new Slot(this.inv, 0, 80, 17)
                .setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(MobBattle.MODID, "gui/armor_slot_sword")));
        this.addSlot(new Slot(this.inv, 1, 80, 35)
                .setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD));
        this.addSlot(new Slot(this.inv, 2, 44, 17) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

        }.setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET));
        this.addSlot(new Slot(this.inv, 3, 44, 35) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().canEquip(stack, MobBattle.slot[3], living);
            }
        }.setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE));
        this.addSlot(new Slot(this.inv, 4, 116, 17) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().canEquip(stack, MobBattle.slot[4], living);
            }
        }.setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS));
        this.addSlot(new Slot(this.inv, 5, 116, 35) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().canEquip(stack, MobBattle.slot[5], living);
            }
        }.setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS));
        for (int column = 0; column < 3; ++column) {
            for (int row = 0; row < 9; ++row) {
                this.addSlot(new Slot(playerInv, row + column * 9 + 9, 8 + row * 18, 66 + column * 18));
            }
        }

        for (int rowHotBar = 0; rowHotBar < 9; ++rowHotBar) {
            this.addSlot(new Slot(playerInv, rowHotBar, 8 + rowHotBar * 18, 124));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            int size = itemstack1.getCount();
            itemstack = itemstack1.copy();
            if (index < 6) {
                if (!this.mergeItemStack(itemstack1, 6, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.inventorySlots.get(2).getHasStack() && itemstack1.getItem().canEquip(itemstack1, MobBattle.slot[2], null)) {
                Slot slot1 = (this.inventorySlots.get(2));
                slot1.putStack(itemstack);
                slot1.onSlotChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else if (!this.inventorySlots.get(3).getHasStack() && itemstack1.getItem().canEquip(itemstack1, MobBattle.slot[3], null)) {
                Slot slot1 = (this.inventorySlots.get(3));
                slot1.putStack(itemstack);
                slot1.onSlotChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else if (!this.inventorySlots.get(4).getHasStack() && itemstack1.getItem().canEquip(itemstack1, MobBattle.slot[4], null)) {
                Slot slot1 = (this.inventorySlots.get(4));
                slot1.putStack(itemstack);
                slot1.onSlotChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else if (!this.inventorySlots.get(5).getHasStack() && itemstack1.getItem().canEquip(itemstack1, MobBattle.slot[5], null)) {
                Slot slot1 = (this.inventorySlots.get(5));
                slot1.putStack(itemstack);
                slot1.onSlotChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else {
                if (!this.mergeItemStack(itemstack1, 0, 2, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

}
