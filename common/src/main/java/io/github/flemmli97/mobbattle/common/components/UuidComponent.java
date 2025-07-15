package io.github.flemmli97.mobbattle.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record UuidComponent(Optional<UUID> uuid, Optional<Component> name) {

    public static final UuidComponent EMPTY = new UuidComponent(Optional.empty(), Optional.empty());
    public static final Codec<UuidComponent> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(UUIDUtil.CODEC.optionalFieldOf("uuids").forGetter(UuidComponent::uuid),
                    ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(UuidComponent::name)
            ).apply(instance, UuidComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UuidComponent> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public UuidComponent decode(RegistryFriendlyByteBuf buf) {
            return new UuidComponent(buf.readBoolean() ? Optional.of(FriendlyByteBuf.readUUID(buf)) : Optional.empty(),
                    buf.readBoolean() ? Optional.of(ComponentSerialization.STREAM_CODEC.decode(buf)) : Optional.empty());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UuidComponent component) {
            buf.writeBoolean(component.uuid().isPresent());
            component.uuid().ifPresent(id -> FriendlyByteBuf.writeUUID(buf, id));
            buf.writeBoolean(component.name().isPresent());
            component.name().ifPresent(comp -> ComponentSerialization.STREAM_CODEC.encode(buf, comp));
        }
    };
}
