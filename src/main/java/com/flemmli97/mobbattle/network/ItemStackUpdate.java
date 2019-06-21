package com.flemmli97.mobbattle.network;

import java.util.function.Supplier;

import com.flemmli97.mobbattle.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ItemStackUpdate{

	public NBTTagCompound compound;
		
	public ItemStackUpdate(NBTTagCompound compound)
	{
		this.compound = compound;
	}
	
	public static ItemStackUpdate fromBytes(PacketBuffer buf) {
		return new ItemStackUpdate(buf.readCompoundTag());
	}

	public static void toBytes(ItemStackUpdate msg, PacketBuffer buf) {
		buf.writeCompoundTag(msg.compound);
	}
	
    public static void onMessage(ItemStackUpdate msg, Supplier<NetworkEvent.Context> ctx) {
    	if(ctx.get().getSender()==null || ctx.get().getSender().getHeldItemMainhand().getItem()!=ModItems.mobEffectGiver)
			return;
    	ctx.get().enqueueWork(()->{
    	EntityPlayer player = ctx.get().getSender();
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.isEmpty())
			stack.setTag(msg.compound);
		});
        ctx.get().setPacketHandled(true);
    }
}