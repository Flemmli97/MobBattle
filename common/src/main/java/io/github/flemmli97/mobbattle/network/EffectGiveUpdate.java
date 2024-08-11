package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class EffectGiveUpdate implements CustomPacketPayload {

    public static final Type<EffectGiveUpdate> TYPE = new Type<>(MobBattle.of("effect_update"));

    public static final StreamCodec<FriendlyByteBuf, EffectGiveUpdate> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public EffectGiveUpdate decode(FriendlyByteBuf buf) {
            return new EffectGiveUpdate(EffectComponent.STREAM_CODEC.decode(buf));
        }

        @Override
        public void encode(FriendlyByteBuf buf, EffectGiveUpdate pkt) {
            EffectComponent.STREAM_CODEC.encode(buf, pkt.data);
        }
    };

    public final EffectComponent data;

    public EffectGiveUpdate(EffectComponent compound) {
        this.data = compound;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
