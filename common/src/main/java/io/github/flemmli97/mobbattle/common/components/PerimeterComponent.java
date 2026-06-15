package io.github.flemmli97.mobbattle.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.mobbattle.common.utils.PerimeterData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PerimeterComponent(PerimeterData.Shape shape, int inner, int outer) {

    public static final PerimeterComponent DEFAULT = new PerimeterComponent(PerimeterData.Shape.SQUARE, 64, 96);

    public static final Codec<PerimeterComponent> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(Codec.stringResolver(PerimeterData.Shape::toString, PerimeterData.Shape::valueOf).fieldOf("shape").forGetter(PerimeterComponent::shape),
                    Codec.INT.fieldOf("inner").forGetter(PerimeterComponent::inner),
                    Codec.INT.fieldOf("outer").forGetter(PerimeterComponent::outer)
            ).apply(instance, PerimeterComponent::new));

    public static final StreamCodec<FriendlyByteBuf, PerimeterComponent> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public PerimeterComponent decode(FriendlyByteBuf buf) {
            return new PerimeterComponent(buf.readEnum(PerimeterData.Shape.class), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, PerimeterComponent component) {
            buf.writeEnum(component.shape());
            buf.writeInt(component.inner());
            buf.writeInt(component.outer());
        }
    };

    public PerimeterData.Perimeter toPerimeter(BlockPos center) {
        return new PerimeterData.Perimeter(center, this.shape, this.inner, Math.max(this.inner, this.outer));
    }

    public PerimeterComponent withShape(PerimeterData.Shape shape) {
        return new PerimeterComponent(shape, this.inner, this.outer);
    }

    public PerimeterComponent withOuter(int outer) {
        return new PerimeterComponent(this.shape, this.inner, Math.max(this.inner, outer));
    }

    public PerimeterComponent withInner(int inner) {
        return new PerimeterComponent(this.shape, inner, Math.max(inner, this.outer));
    }
}
