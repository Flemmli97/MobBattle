package com.flemmli97.mobbattle.network;

import com.flemmli97.mobbattle.MobBattle;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final SimpleChannel dispatcher = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MobBattle.MODID, "packets"))
            .clientAcceptedVersions(a -> true).serverAcceptedVersions(a -> true).networkProtocolVersion(() -> "1").simpleChannel();

    public static void register() {
        int id = 0;
        dispatcher.registerMessage(id++, EquipMessage.class, EquipMessage::toBytes, EquipMessage::fromBytes, EquipMessage::onMessage);
        dispatcher.registerMessage(id++, ItemStackUpdate.class, ItemStackUpdate::toBytes, ItemStackUpdate::fromBytes, ItemStackUpdate::onMessage);
        dispatcher.registerMessage(id++, PacketOpenGuiArmor.class, PacketOpenGuiArmor::toBytes, PacketOpenGuiArmor::fromBytes,
                PacketOpenGuiArmor::onMessage);

    }

    public static final <T> void sendToServer(T message) {
        dispatcher.sendToServer(message);
    }

    public static final <T> void sendToClient(T message, ServerPlayerEntity player) {
        dispatcher.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
