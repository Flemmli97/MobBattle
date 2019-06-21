package com.flemmli97.mobbattle.network;



import java.util.function.Supplier;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.client.gui.GuiArmor;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketOpenGuiArmor{

	private int entityID, windowID;
	
	public PacketOpenGuiArmor(EntityLiving entity, int windowID)
	{
		this.entityID=entity.getEntityId();
		this.windowID=windowID;
	}
	
	private PacketOpenGuiArmor(int entityID, int windowID)
	{
		this.entityID=entityID;
		this.windowID=windowID;
	}
	
	public static PacketOpenGuiArmor fromBytes(PacketBuffer buf) {
		return new PacketOpenGuiArmor(buf.readInt(), buf.readInt());
	}

	public static void toBytes(PacketOpenGuiArmor msg, PacketBuffer buf) {
		buf.writeInt(msg.entityID);
		buf.writeInt(msg.windowID);
	}
	
	public static void onMessage(PacketOpenGuiArmor msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			EntityPlayer player = MobBattle.proxy.getPlayer(ctx);
	    	Entity e = player.world.getEntityByID(msg.entityID);
	    	if(e instanceof EntityLiving)
	    	{
	    		Minecraft.getInstance().displayGuiScreen(new GuiArmor(player.inventory, (EntityLiving) e));
	    		player.openContainer.windowId=msg.windowID;
	    	}
	    });
		ctx.get().setPacketHandled(true);
    }
}