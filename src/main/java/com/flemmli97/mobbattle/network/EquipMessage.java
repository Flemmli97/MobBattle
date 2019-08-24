package com.flemmli97.mobbattle.network;



import java.util.function.Supplier;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class EquipMessage{

	public ItemStack equipment;
	public int entityId;
	public int slot;
	
	public EquipMessage(ItemStack stack, int entityId, int slot)
	{
		this.equipment = stack;
		this.entityId = entityId;
		this.slot = slot;
	}
	
	public static EquipMessage fromBytes(PacketBuffer buf) {
		CompoundNBT compound = buf.readCompoundTag();
		return new EquipMessage(compound.contains("Stack") ? ItemStack.read(compound.getCompound("Stack")) : ItemStack.EMPTY, compound.getInt("EntityID"), compound.getInt("Slot"));
	}

	public static void toBytes(EquipMessage msg, PacketBuffer buf) {
		CompoundNBT compound = new CompoundNBT();
		CompoundNBT tag = new CompoundNBT();
		compound.putInt("EntityID", msg.entityId);
		if(msg.equipment!=null)
			compound.put("Stack", msg.equipment.write(tag));
		compound.putInt("Slot", msg.slot);
		buf.writeCompoundTag(compound);
	}
	
	public static void onMessage(EquipMessage msg, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getSender()==null || ctx.get().getSender().getHeldItemMainhand().getItem()!=ModItems.mobArmor)
			return;
		ctx.get().enqueueWork(()->{
	    	World world = ctx.get().getSender().world;
	    	Entity e = world.getEntityByID(msg.entityId);
	    	if(e instanceof MobEntity)
	    	{
				e.setItemStackToSlot(MobBattle.slot[msg.slot], msg.equipment);
	    	}
		});
		ctx.get().setPacketHandled(true);
    }
}
