package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.ClientPerimeterData;
import io.github.flemmli97.mobbattle.common.utils.PerimeterData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public class S2CPerimeterInfo implements CustomPacketPayload {

    public static final Type<S2CPerimeterInfo> TYPE = new Type<>(MobBattle.of("s2c_perimeter_info"));

    public static final StreamCodec<FriendlyByteBuf, S2CPerimeterInfo> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public S2CPerimeterInfo decode(FriendlyByteBuf buf) {
            return new S2CPerimeterInfo(buf.readBoolean() ? PerimeterData.Perimeter.STREAM_CODEC.decode(buf) : null);
        }

        @Override
        public void encode(FriendlyByteBuf buf, S2CPerimeterInfo pkt) {
            if (pkt.perimeter != null) {
                buf.writeBoolean(true);
                PerimeterData.Perimeter.STREAM_CODEC.encode(buf, pkt.perimeter);
            } else {
                buf.writeBoolean(false);
            }
        }
    };

    private final PerimeterData.Perimeter perimeter;

    public S2CPerimeterInfo(ServerLevel level) {
        this.perimeter = PerimeterData.get(level).perimeter();
    }

    private S2CPerimeterInfo(PerimeterData.Perimeter perimeter) {
        this.perimeter = perimeter;
    }

    public static void handle(S2CPerimeterInfo pkt) {
        ClientPerimeterData.setPerimeter(pkt.perimeter);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}