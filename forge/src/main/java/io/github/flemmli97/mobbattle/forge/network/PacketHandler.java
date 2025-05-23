package io.github.flemmli97.mobbattle.forge.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.network.PacketRegistrar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

    private static final SimpleChannel DISPATCHER = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MobBattle.MODID, "packets"))
            .clientAcceptedVersions(a -> true).serverAcceptedVersions(a -> true).networkProtocolVersion(() -> "1").simpleChannel();

    public static void register() {
        int server = PacketRegistrar.registerServerPackets(new PacketRegistrar.ServerPacketRegister() {
            @Override
            public <P> void registerMessage(int index, ResourceLocation id, Class<P> clss, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, BiConsumer<P, ServerPlayer> handler) {
                DISPATCHER.registerMessage(index, clss, encoder, decoder, handlerServer(handler), Optional.of(NetworkDirection.PLAY_TO_SERVER));
            }
        }, 0);
        PacketRegistrar.registerClientPackets(new PacketRegistrar.ClientPacketRegister() {
            @Override
            public <P> void registerMessage(int index, ResourceLocation id, Class<P> clss, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, Consumer<P> handler) {
                DISPATCHER.registerMessage(index, clss, encoder, decoder, handlerClient(handler), Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            }
        }, server);
    }

    public static <T> void sendToServer(T message) {
        DISPATCHER.sendToServer(message);
    }

    public static <T> void sendToClient(T message, ServerPlayer player) {
        DISPATCHER.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> handlerServer(BiConsumer<T, ServerPlayer> handler) {
        return (p, ctx) -> {
            ctx.get().enqueueWork(() -> handler.accept(p, ctx.get().getSender()));
            ctx.get().setPacketHandled(true);
        };
    }

    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> handlerClient(Consumer<T> handler) {
        return (p, ctx) -> {
            ctx.get().enqueueWork(() -> handler.accept(p));
            ctx.get().setPacketHandled(true);
        };
    }
}
