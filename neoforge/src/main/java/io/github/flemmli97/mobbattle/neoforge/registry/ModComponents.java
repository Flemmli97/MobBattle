package io.github.flemmli97.mobbattle.neoforge.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.components.UuidComponent;
import io.github.flemmli97.mobbattle.components.UuidListComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MobBattle.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AreaPositionComponent>> BOX = COMPONENTS.register("box",
            () -> new DataComponentType.Builder<AreaPositionComponent>().persistent(AreaPositionComponent.CODEC).networkSynchronized(AreaPositionComponent.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectComponent>> EFFECT = COMPONENTS.register("effect",
            () -> new DataComponentType.Builder<EffectComponent>().persistent(EffectComponent.CODEC).networkSynchronized(EffectComponent.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UuidComponent>> SELECTED_MOB = COMPONENTS.register("selected_mob",
            () -> new DataComponentType.Builder<UuidComponent>().persistent(UuidComponent.CODEC).networkSynchronized(UuidComponent.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UuidListComponent>> SELECTED_MOBS = COMPONENTS.register("selected_mobs",
            () -> new DataComponentType.Builder<UuidListComponent>().persistent(UuidListComponent.CODEC).networkSynchronized(UuidListComponent.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpawnEggOptions>> SPAWN_EGG_OPTIONS = COMPONENTS.register("spawnegg_options",
            () -> new DataComponentType.Builder<SpawnEggOptions>().persistent(SpawnEggOptions.CODEC).networkSynchronized(SpawnEggOptions.STREAM_CODEC).build());

}
