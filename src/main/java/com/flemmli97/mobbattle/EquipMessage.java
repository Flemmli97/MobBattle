package com.flemmli97.mobbattle;



import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class EquipMessage{

	public ItemStack equipment;
	public String uuid;
	public int slot;
	
	public EquipMessage(ItemStack stack, String entityUUID, int slot)
	{
		this.equipment = stack;
		this.uuid = entityUUID;
		this.slot = slot;
	}
	
	public static EquipMessage fromBytes(PacketBuffer buf) {
		NBTTagCompound compound = buf.readCompoundTag();
		return new EquipMessage(compound.hasKey("Stack") ? ItemStack.read((NBTTagCompound) compound.getTag("Stack")) : ItemStack.EMPTY, compound.getString("UUID"), compound.getInt("Slot"));
	}

	public static void toBytes(EquipMessage msg, PacketBuffer buf) {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		compound.setString("UUID", msg.uuid);
		if(msg.equipment!=null)
		compound.setTag("Stack", msg.equipment.write(tag));
		compound.setInt("Slot", msg.slot);
		buf.writeCompoundTag(tag);
	}
	
	public static void onMessage(EquipMessage msg, Supplier<NetworkEvent.Context> ctx) {
    	World world = ctx.get().getSender().world;
    	for (final Object obj : world.loadedEntityList)
		{
    		if(obj instanceof EntityLiving)
    		{
    			EntityLiving entity = (EntityLiving) obj;
				if (entity.getUniqueID().equals(UUID.fromString(msg.uuid)))
				{
					entity.setItemStackToSlot(ServerProxy.slot[msg.slot], msg.equipment);
				}
			}
		}
    	ctx.get().setPacketHandled(true);
    }
}