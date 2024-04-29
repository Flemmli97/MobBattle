package io.github.flemmli97.mobbattle.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AreaPositionComponent(@Nullable BlockPos first, @Nullable BlockPos second) {

    public static final AreaPositionComponent DEFAULT = new AreaPositionComponent(null, null);
    public static final Codec<AreaPositionComponent> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(BlockPos.CODEC.optionalFieldOf("first").forGetter(d -> Optional.ofNullable(d.first())),
                    BlockPos.CODEC.optionalFieldOf("second").forGetter(d -> Optional.ofNullable(d.second()))
            ).apply(instance, (p1, p2) -> new AreaPositionComponent(p1.orElse(null), p2.orElse(null))));

    public static final StreamCodec<ByteBuf, AreaPositionComponent> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public AreaPositionComponent decode(ByteBuf buf) {
            return new AreaPositionComponent(FriendlyByteBuf.readNullable(buf, BlockPos.STREAM_CODEC),
                    FriendlyByteBuf.readNullable(buf, BlockPos.STREAM_CODEC));
        }

        @Override
        public void encode(ByteBuf buf, AreaPositionComponent component) {
            FriendlyByteBuf.writeNullable(buf, component.first(), BlockPos.STREAM_CODEC);
            FriendlyByteBuf.writeNullable(buf, component.second(), BlockPos.STREAM_CODEC);
        }
    };

    public AreaPositionComponent withFirst(BlockPos pos) {
        return new AreaPositionComponent(pos, this.second);
    }

    public AreaPositionComponent withSecond(BlockPos pos) {
        return new AreaPositionComponent(this.first, pos);
    }
}
