package io.github.flemmli97.mobbattle.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SpawnEggOptions(@Nullable String team, int amount, int spacing) {

    public static final SpawnEggOptions DEFAULT = new SpawnEggOptions(null, 1, 0);
    public static final Codec<SpawnEggOptions> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(Codec.STRING.optionalFieldOf("team").forGetter(d -> Optional.ofNullable(d.team())),
                    ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(SpawnEggOptions::amount),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("spacing").forGetter(SpawnEggOptions::spacing)
            ).apply(instance, (team, amount, spacing) -> new SpawnEggOptions(team.orElse(null), amount, spacing)));

    public static final StreamCodec<FriendlyByteBuf, SpawnEggOptions> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public SpawnEggOptions decode(FriendlyByteBuf buf) {
            return new SpawnEggOptions(buf.readNullable(FriendlyByteBuf::readUtf),
                    buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, SpawnEggOptions component) {
            buf.writeNullable(component.team(), FriendlyByteBuf::writeUtf);
            buf.writeInt(component.amount());
            buf.writeInt(component.spacing());
        }
    };

    public SpawnEggOptions(@Nullable String team, int amount, int spacing) {
        this.team = team;
        this.amount = Mth.clamp(amount, 0, 100);
        this.spacing = Mth.clamp(spacing, 0, 99);
    }
}
