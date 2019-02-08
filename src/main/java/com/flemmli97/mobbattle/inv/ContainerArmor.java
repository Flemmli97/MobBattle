package com.flemmli97.mobbattle.inv;

import com.flemmli97.mobbattle.CommonProxy;
import com.flemmli97.mobbattle.MobBattle;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerArmor extends Container{
	
	public ContainerArmor(InventoryPlayer playerInv, EntityLiving living)
	{
		InventoryArmor inv = new InventoryArmor(living);
		inv.openInventory(playerInv.player);
        this.addSlotToContainer(new Slot(inv, 0, 80, 17)
	    {
			@Override
			@SideOnly(Side.CLIENT)
	        public String getSlotTexture()
	        {
	            return MobBattle.MODID + ":gui/armor_slot_sword";
	        }

        });
        this.addSlotToContainer(new Slot(inv, 1, 80, 35)
        {
             @Override
        	 @SideOnly(Side.CLIENT)
             public String getSlotTexture()
             {
                 return "minecraft:items/empty_armor_slot_shield";
             }
        });
        this.addSlotToContainer(new Slot(inv, 2, 44, 17)
        {
        	@Override
    		public int getSlotStackLimit()
	        {
	            return 1;
	        }

            @Override
    		@SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_helmet";
            }
        });
        this.addSlotToContainer(new Slot(inv, 3, 44, 35)
        {
        	@Override
    		public int getSlotStackLimit()
	        {
	            return 1;
	        }
        	@Override
			public boolean isItemValid(ItemStack stack) {
        		if (stack == null)
                {
                    return false;
                }
                else
                {
                    return stack.getItem().isValidArmor(stack, CommonProxy.slot[3], living);
                }
			}

            @Override
			@SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_chestplate";
            }
        });
        this.addSlotToContainer(new Slot(inv, 4, 116, 17)
        {
	        	@Override
	    		public int getSlotStackLimit()
	        {
	            return 1;
	        }
	        	@Override
			public boolean isItemValid(ItemStack stack) {
        		if (stack == null)
                {
                    return false;
                }
                else
                {
                    return stack.getItem().isValidArmor(stack, CommonProxy.slot[4], living);
                }
			}
        	@Override
			@SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_leggings";
            }
        });
        this.addSlotToContainer(new Slot(inv, 5, 116, 35)
        {
	        	@Override
	    		public int getSlotStackLimit()
	        {
	            return 1;
	        }
	        	@Override
			public boolean isItemValid(ItemStack stack) {
        		if (stack == null)
                {
                    return false;
                }
                else
                {
                    return stack.getItem().isValidArmor(stack, CommonProxy.slot[5], living);
                }
			}
        	@Override
			@SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_boots";
            }
        });
		for (int column = 0; column < 3; ++column)
        {
            for (int row = 0; row < 9; ++row)
            {
                this.addSlotToContainer(new Slot(playerInv, row + column * 9 + 9, 8 + row * 18, 66 + column * 18));
            }
        }

        for (int rowHotBar = 0; rowHotBar < 9; ++rowHotBar)
        {
            this.addSlotToContainer(new Slot(playerInv, rowHotBar, 8 + rowHotBar * 18, 124));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            int size = itemstack1.getCount();
            itemstack = itemstack1.copy();
            if (index < 6)
            {
                if (!this.mergeItemStack(itemstack1, 6, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!((Slot)this.inventorySlots.get(2)).getHasStack() && itemstack1.getItem().isValidArmor(itemstack1, CommonProxy.slot[2], null))
            {
            	Slot slot1 = ((Slot)this.inventorySlots.get(2));
            	slot1.putStack(itemstack);
            	slot1.onSlotChanged();
            	size--;
            	itemstack1.setCount(size); 
                return ItemStack.EMPTY;
            }
            else if(!((Slot)this.inventorySlots.get(3)).getHasStack() && itemstack1.getItem().isValidArmor(itemstack1, CommonProxy.slot[3], null))
            {
            	Slot slot1 = ((Slot)this.inventorySlots.get(3));
            	slot1.putStack(itemstack);
            	slot1.onSlotChanged();
            	size--;
            	itemstack1.setCount(size); 
                return ItemStack.EMPTY;
            }
            else if(!((Slot)this.inventorySlots.get(4)).getHasStack() && itemstack1.getItem().isValidArmor(itemstack1, CommonProxy.slot[4], null))
            {
            	Slot slot1 = ((Slot)this.inventorySlots.get(4));
            	slot1.putStack(itemstack);
            	slot1.onSlotChanged();
            	size--;
            	itemstack1.setCount(size); 
                return ItemStack.EMPTY;            
            }
            else if(!((Slot)this.inventorySlots.get(5)).getHasStack() && itemstack1.getItem().isValidArmor(itemstack1, CommonProxy.slot[5], null))
            {
            	Slot slot1 = ((Slot)this.inventorySlots.get(5));
            	slot1.putStack(itemstack);
            	slot1.onSlotChanged();
            	size--;
            	itemstack1.setCount(size); 
                return ItemStack.EMPTY;
            }
            else
            {
            		if (!this.mergeItemStack(itemstack1, 0, 2, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }
		
        return itemstack;
	}
	
	
}
