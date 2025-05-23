package io.github.flemmli97.mobbattle.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PacketRegistrar {

    public static int registerServerPackets(ServerPacketRegister register, int id) {
        register.registerMessage(id++, C2SEffectStack.ID, C2SEffectStack.class, C2SEffectStack::write, C2SEffectStack::read, C2SEffectStack::handle);
        register.registerMessage(id++, C2SSpawnEgg.ID, C2SSpawnEgg.class, C2SSpawnEgg::write, C2SSpawnEgg::read, C2SSpawnEgg::handle);
        return id;
    }

    public static int registerClientPackets(ClientPacketRegister register, int id) {
        register.registerMessage(id++, S2CSpawnEggScreen.ID, S2CSpawnEggScreen.class, S2CSpawnEggScreen::write, S2CSpawnEggScreen::read, S2CSpawnEggScreen::handle);
        return id;
    }

    public interface ServerPacketRegister {
        <P> void registerMessage(int index, ResourceLocation id, Class<P> clss, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, BiConsumer<P, ServerPlayer> handler);
    }

    public interface ClientPacketRegister {
        <P> void registerMessage(int index, ResourceLocation id, Class<P> clss, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, Consumer<P> handler);
    }
}
