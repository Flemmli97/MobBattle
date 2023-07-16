package io.github.flemmli97.mobbattle.forge.data;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class EntityTagGen extends IntrinsicHolderTagsProvider<EntityType<?>> {

    @SuppressWarnings("deprecation")
    public EntityTagGen(PackOutput arg2, CompletableFuture<HolderLookup.Provider> completableFuture, ExistingFileHelper existingFileHelper) {
        super(arg2, Registries.ENTITY_TYPE, completableFuture, (arg) -> arg.builtInRegistryHolder().key(), MobBattle.MODID, existingFileHelper);
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
