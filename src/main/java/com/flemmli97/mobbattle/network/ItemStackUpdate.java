package com.flemmli97.mobbattle.network;

import java.util.function.Supplier;

import com.flemmli97.mobbattle.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ItemStackUpdate{

	public CompoundNBT compound;
		
	public ItemStackUpdate(CompoundNBT compound)
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
    	PlayerEntity player = ctx.get().getSender();
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.isEmpty())
			stack.setTag(msg.compound);
		});
        ctx.get().setPacketHandled(true);
    }
}
