package com.flemmli97.mobbattle;

import java.util.function.Supplier;

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
    	EntityPlayer player = ctx.get().getSender();
    	if(player!=null)
    	{
    		ItemStack stack = player.getHeldItemMainhand();
    		if(!stack.isEmpty())
    			stack.setTag(msg.compound);
    	}
        ctx.get().setPacketHandled(true);
    }
}