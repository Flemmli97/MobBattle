package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class EntityTagGen extends IntrinsicHolderTagsProvider<EntityType<?>> {

    public EntityTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENTITY_TYPE, lookupProvider, (t) -> t.builtInRegistryHolder().key(), MobBattle.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(MobBattle.HURT_IGNORED)
                .add(EntityType.VEX);
    }

    @Override
    public String getName() {
        return "Entity Tags";
    }
}
