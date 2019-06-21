package com.flemmli97.mobbattle.network;



import java.util.function.Supplier;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
		NBTTagCompound compound = buf.readCompoundTag();
		return new EquipMessage(compound.hasKey("Stack") ? ItemStack.read(compound.getCompound("Stack")) : ItemStack.EMPTY, compound.getInt("EntityID"), compound.getInt("Slot"));
	}

	public static void toBytes(EquipMessage msg, PacketBuffer buf) {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		compound.setInt("EntityID", msg.entityId);
		if(msg.equipment!=null)
			compound.setTag("Stack", msg.equipment.write(tag));
		compound.setInt("Slot", msg.slot);
		buf.writeCompoundTag(compound);
	}
	
	public static void onMessage(EquipMessage msg, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getSender()==null || ctx.get().getSender().getHeldItemMainhand().getItem()!=ModItems.mobArmor)
			return;
		ctx.get().enqueueWork(()->{
	    	World world = ctx.get().getSender().world;
	    	Entity e = world.getEntityByID(msg.entityId);
			System.out.println(e + " id " + msg.entityId);
	    	if(e instanceof EntityLiving)
	    	{
				e.setItemStackToSlot(MobBattle.slot[msg.slot], msg.equipment);
	    	}
		});
		ctx.get().setPacketHandled(true);
    }
}