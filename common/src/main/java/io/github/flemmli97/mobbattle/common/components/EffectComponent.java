package io.github.flemmli97.mobbattle.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record EffectComponent(Optional<Holder<MobEffect>> effect, int duration, int amplifier, boolean particles) {

    public static final EffectComponent DEFAULT = new EffectComponent(Optional.empty(), 0, 0, true);

    public static final Codec<EffectComponent> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(BuiltInRegistries.MOB_EFFECT.holderByNameCodec().optionalFieldOf("effect").forGetter(EffectComponent::effect),
                    Codec.INT.optionalFieldOf("duration").forGetter(inst -> inst.duration() == 0 ? Optional.empty() : Optional.of(inst.duration())),
                    Codec.INT.optionalFieldOf("amplifier").forGetter(inst -> inst.amplifier() == 0 ? Optional.empty() : Optional.of(inst.amplifier())),
                    Codec.BOOL.optionalFieldOf("particles").forGetter(inst -> inst.particles() ? Optional.empty() : Optional.of(false))
            ).apply(instance, (effect, duration, amp, particles) -> new EffectComponent(effect, duration.orElse(0), amp.orElse(0), particles.orElse(true))));

    public static final StreamCodec<ByteBuf, EffectComponent> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public EffectComponent decode(ByteBuf byteBuf) {
            return new EffectComponent(byteBuf.readBoolean() ? BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(Utf8String.read(byteBuf, 32767))).map(r -> r) : Optional.empty(),
                    byteBuf.readInt(), byteBuf.readInt(), byteBuf.readBoolean());
        }

        @Override
        public void encode(ByteBuf byteBuf, EffectComponent component) {
            byteBuf.writeBoolean(component.effect().isPresent());
            component.effect().ifPresent(eff -> Utf8String.write(byteBuf, eff.getRegisteredName(), 32767));
            byteBuf.writeInt(component.duration());
            byteBuf.writeInt(component.amplifier());
            byteBuf.writeBoolean(component.particles());
        }
    };

    public EffectComponent withEffect(Holder<MobEffect> effect) {
        return new EffectComponent(Optional.of(effect), this.duration, this.amplifier, this.particles);
    }

    public EffectComponent withDuration(int duration) {
        return new EffectComponent(this.effect, duration, this.amplifier, this.particles);
    }

    public EffectComponent withAmplifier(int amplifier) {
        return new EffectComponent(this.effect, this.duration, amplifier, this.particles);
    }

    public EffectComponent withParticles(boolean particles) {
        return new EffectComponent(this.effect, this.duration, this.amplifier, particles);
    }
}
