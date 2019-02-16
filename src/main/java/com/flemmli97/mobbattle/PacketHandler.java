package com.flemmli97.mobbattle;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

	private static final SimpleChannel dispatcher = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MobBattle.MODID, "packets"))
			.clientAcceptedVersions(a -> true)
            .serverAcceptedVersions(a -> true)
            .networkProtocolVersion(() -> NetworkHooks.NOVERSION)
            .simpleChannel();
	
	public static final <T> void sendToServer(T message) {
		dispatcher.sendToServer(message);
	}
	
	static
	{
		int id = 0;
		dispatcher.registerMessage(id++, EquipMessage.class, EquipMessage::toBytes, EquipMessage::fromBytes, EquipMessage::onMessage);
		dispatcher.registerMessage(id++,ItemStackUpdate.class, ItemStackUpdate::toBytes, ItemStackUpdate::fromBytes, ItemStackUpdate::onMessage);
	}
}
