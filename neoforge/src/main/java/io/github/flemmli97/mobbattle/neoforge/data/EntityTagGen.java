package io.github.flemmli97.mobbattle.neoforge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class EntityTagGen extends IntrinsicHolderTagsProvider<EntityType<?>> {

    @SuppressWarnings("deprecation")
    public EntityTagGen(PackOutput arg2, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(arg2, Registries.ENTITY_TYPE, completableFuture, (arg) -> arg.builtInRegistryHolder().key(), MobBattle.MODID);
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
