package io.github.flemmli97.mobbattle.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.network.S2CPerimeterInfo;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class PerimeterData extends SavedData {

    public static final ResourceLocation ID = MobBattle.of("perimeter");
    private static final SavedData.Factory<PerimeterData> TYPE = new SavedData.Factory<>(PerimeterData::new, PerimeterData::new, DataFixTypes.LEVEL);

    private Perimeter perimeter = null;

    private PerimeterData() {
    }

    private PerimeterData(CompoundTag tag, HolderLookup.Provider provider) {
        this.perimeter = tag.contains("Perimeter") ? Perimeter.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("Perimeter")).getOrThrow() : null;
    }

    public static PerimeterData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE, String.format("%s_%s", ID.getNamespace(), ID.getPath()));
    }

    public Perimeter perimeter() {
        return this.perimeter;
    }

    public void setPerimeter(ServerLevel level, Perimeter perimeter) {
        this.perimeter = perimeter;
        for (ServerPlayer player : level.players()) {
            if (player.getMainHandItem().is(MobBattleItems.PERIMETER_TOOL.get()) || player.getOffhandItem().is(MobBattleItems.PERIMETER_TOOL.get())) {
                CrossPlatformStuff.INSTANCE.sendToClient(new S2CPerimeterInfo(player.serverLevel()), player);
            }
        }
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        if (this.perimeter != null) {
            tag.put("Perimeter", Perimeter.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.perimeter).getOrThrow());
        }
        return tag;
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
