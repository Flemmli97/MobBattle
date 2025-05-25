package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

public class S2CSpawnEggScreen implements CustomPacketPayload {

    public static final Type<S2CSpawnEggScreen> TYPE = new Type<>(MobBattle.of("s2c_spawn_egg_screen"));

    public static final StreamCodec<FriendlyByteBuf, S2CSpawnEggScreen> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public S2CSpawnEggScreen decode(FriendlyByteBuf buf) {
            return new S2CSpawnEggScreen(buf.readEnum(InteractionHand.class));
        }

        @Override
        public void encode(FriendlyByteBuf buf, S2CSpawnEggScreen pkt) {
            buf.writeEnum(pkt.hand);
        }
    };

    private final InteractionHand hand;

    public S2CSpawnEggScreen(InteractionHand hand) {
        this.hand = hand;
    }

    public static void handle(S2CSpawnEggScreen pkt) {
        ClientHandler.openSpawneggGui(pkt.hand);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}