package io.github.flemmli97.mobbattle.inv;

import com.mojang.datafixers.util.Pair;
import io.github.flemmli97.mobbattle.CrossPlatformStuff;
import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ContainerArmor extends AbstractContainerMenu {

    private InventoryArmor inv;

    public ContainerArmor(int windowID, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowID, playerInv, playerInv.player.level.getEntity(buf.readInt()));
    }

    public ContainerArmor(int windowID, Inventory playerInv, Entity e) {
        super(CrossPlatformStuff.getArmorMenuType(), windowID);
        if (!(e instanceof Mob))
            return;
        Mob living = (Mob) e;
        this.inv = new InventoryArmor(living);
        this.inv.startOpen(playerInv.player);
        this.addSlot(new Slot(this.inv, 0, 80, 17) {
            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MobBattle.MODID, "gui/armor_slot_sword"));
            }
        });
        this.addSlot(new Slot(this.inv, 1, 80, 35) {
            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
        this.addSlot(new Slot(this.inv, 2, 44, 17) {

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
            }
        });
        this.addSlot(new Slot(this.inv, 3, 44, 35) {

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return CrossPlatformStuff.canEquip(stack, MobBattle.slot[3], living);
            }

            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
            }
        });
        this.addSlot(new Slot(this.inv, 4, 116, 17) {

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return CrossPlatformStuff.canEquip(stack, MobBattle.slot[4], living);
            }

            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
            }
        });
        this.addSlot(new Slot(this.inv, 5, 116, 35) {

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return CrossPlatformStuff.canEquip(stack, MobBattle.slot[5], living);
            }

            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
            }
        });
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
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            int size = itemstack1.getCount();
            itemstack = itemstack1.copy();
            if (index < 6) {
                if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.slots.get(2).hasItem() && CrossPlatformStuff.canEquip(itemstack1, MobBattle.slot[2], null)) {
                Slot slot1 = (this.slots.get(2));
                slot1.set(itemstack);
                slot1.setChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else if (!this.slots.get(3).hasItem() && CrossPlatformStuff.canEquip(itemstack1, MobBattle.slot[3], null)) {
                Slot slot1 = (this.slots.get(3));
                slot1.set(itemstack);
                slot1.setChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else if (!this.slots.get(4).hasItem() && CrossPlatformStuff.canEquip(itemstack1, MobBattle.slot[4], null)) {
                Slot slot1 = (this.slots.get(4));
                slot1.set(itemstack);
                slot1.setChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else if (!this.slots.get(5).hasItem() && CrossPlatformStuff.canEquip(itemstack1, MobBattle.slot[5], null)) {
                Slot slot1 = (this.slots.get(5));
                slot1.set(itemstack);
                slot1.setChanged();
                size--;
                itemstack1.setCount(size);
                return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 2, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

}
