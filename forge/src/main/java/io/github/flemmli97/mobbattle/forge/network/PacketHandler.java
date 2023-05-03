package io.github.flemmli97.mobbattle.forge.network;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final SimpleChannel dispatcher = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MobBattle.MODID, "packets"))
            .clientAcceptedVersions(a -> true).serverAcceptedVersions(a -> true).networkProtocolVersion(() -> "1").simpleChannel();

    public static void register() {
        int id = 0;
        dispatcher.registerMessage(id++, EquipMessage.class, EquipMessage::toBytes, EquipMessage::fromBytes, EquipMessage::onMessage);
        dispatcher.registerMessage(id++, ItemStackUpdate.class, ItemStackUpdate::toBytes, ItemStackUpdate::fromBytes, ItemStackUpdate::onMessage);
    }

    public static <T> void sendToServer(T message) {
        dispatcher.sendToServer(message);
    }

    public static <T> void sendToClient(T message, ServerPlayer player) {
        dispatcher.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
