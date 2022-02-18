package io.github.flemmli97.mobbattle.fabric.platform;

import io.github.flemmli97.mobbattle.SimpleRegistryWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public record FabricRegistryWrapper<T>(Registry<T> delegate) implements SimpleRegistryWrapper<T> {

    @Override
    public T getFromId(ResourceLocation id) {
        return this.delegate.get(id);
    }

    @Override
    public ResourceLocation getIDFrom(T entry) {
        return this.delegate.getKey(entry);
    }

    @Override
    public Iterable<T> getIterator() {
        return this.delegate;
    }
}
