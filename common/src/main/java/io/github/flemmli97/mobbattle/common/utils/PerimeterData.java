package io.github.flemmli97.mobbattle.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.network.S2CPerimeterInfo;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class PerimeterData extends SavedData {

    public static final Codec<PerimeterData> CODEC = Perimeter.CODEC.optionalFieldOf("Perimeter")
            .xmap(PerimeterData::new, d -> Optional.ofNullable(d.perimeter)).codec();

    private static final SavedDataType<PerimeterData> TYPE = new SavedDataType<>(MobBattle.of("perimeter"), PerimeterData::new, CODEC, DataFixTypes.LEVEL);

    private Perimeter perimeter = null;

    private PerimeterData() {
    }

    private PerimeterData(Optional<Perimeter> perimeter) {
        this.perimeter = perimeter.orElse(null);
    }

    public static PerimeterData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Perimeter perimeter() {
        return this.perimeter;
    }

    public void setPerimeter(ServerLevel level, Perimeter perimeter) {
        this.perimeter = perimeter;
        for (ServerPlayer player : level.players()) {
            if (player.getMainHandItem().is(MobBattleItems.PERIMETER_TOOL.get()) || player.getOffhandItem().is(MobBattleItems.PERIMETER_TOOL.get())) {
                CrossPlatformStuff.INSTANCE.sendToClient(new S2CPerimeterInfo(player.level()), player);
            }
        }
        this.setDirty();
    }

    public record Perimeter(BlockPos center, Shape shape, int inner, int outer) {

        public static final Codec<Perimeter> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BlockPos.CODEC.fieldOf("center").forGetter(Perimeter::center),
                        Codec.stringResolver(Shape::toString, Shape::valueOf).fieldOf("shape").forGetter(Perimeter::shape),
                        Codec.INT.fieldOf("inner").forGetter(Perimeter::inner),
                        Codec.INT.fieldOf("outer").forGetter(Perimeter::outer)
                ).apply(instance, Perimeter::new));

        public static final StreamCodec<FriendlyByteBuf, Perimeter> STREAM_CODEC = new StreamCodec<>() {

            @Override
            public Perimeter decode(FriendlyByteBuf buf) {
                return new Perimeter(buf.readBlockPos(), buf.readEnum(PerimeterData.Shape.class), buf.readInt(), buf.readInt());
            }

            @Override
            public void encode(FriendlyByteBuf buf, Perimeter component) {
                buf.writeBlockPos(component.center());
                buf.writeEnum(component.shape());
                buf.writeInt(component.inner());
                buf.writeInt(component.outer());
            }
        };

        public boolean contains(Vec3 pos) {
            return switch (this.shape()) {
                case SQUARE -> {
                    double dist = this.distAxis(pos, Vec3.atCenterOf(this.center));
                    yield dist <= this.inner;
                }
                case CIRCLE -> {
                    double dist = this.distHorizontalSqr(pos, Vec3.atCenterOf(this.center));
                    yield dist <= this.inner * this.inner;
                }
            };
        }

        public boolean shouldTeleport(Vec3 pos) {
            return switch (this.shape()) {
                case SQUARE -> {
                    double dist = this.distAxis(pos, Vec3.atCenterOf(this.center));
                    yield dist > this.outer;
                }
                case CIRCLE -> {
                    double dist = this.distHorizontalSqr(pos, Vec3.atCenterOf(this.center));
                    yield dist > this.outer * this.outer;
                }
            };
        }

        private double distHorizontalSqr(Vec3 from, Vec3 to) {
            double xd = from.x() - to.x();
            double zd = from.z() - to.z();
            return xd * xd + zd * zd;
        }

        private double distAxis(Vec3 from, Vec3 to) {
            double xd = Math.abs(to.x() - from.x());
            double zd = Math.abs(to.z() - from.z());
            return Math.max(xd, zd);
        }
    }

    public enum Shape {
        CIRCLE("mobbattle.shape.circle"),
        SQUARE("mobbattle.shape.square");

        public final String translationKey;

        Shape(String translationKey) {
            this.translationKey = translationKey;
        }
    }
}
