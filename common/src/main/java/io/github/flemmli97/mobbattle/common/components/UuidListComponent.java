package io.github.flemmli97.mobbattle.common.components;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class UuidListComponent {

    public static final UuidListComponent EMPTY = new UuidListComponent(List.of());

    public static final Codec<UuidListComponent> CODEC = UUIDUtil.CODEC.listOf().fieldOf("uuids")
            .xmap(UuidListComponent::new, UuidListComponent::uuids).codec();
    public static final StreamCodec<ByteBuf, UuidListComponent> STREAM_CODEC = UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(UuidListComponent::new, UuidListComponent::uuids);

    private final ImmutableList<UUID> uuids;

    public UuidListComponent(List<UUID> uuids) {
        this.uuids = ImmutableList.copyOf(uuids);
    }

    public UuidListComponent update(Consumer<List<UUID>> cons) {
        List<UUID> list = new ArrayList<>(this.uuids);
        cons.accept(list);
        return new UuidListComponent(list);
    }

    public ImmutableList<UUID> uuids() {
        return this.uuids;
    }
}
