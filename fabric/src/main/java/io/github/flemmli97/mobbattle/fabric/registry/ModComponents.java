package io.github.flemmli97.mobbattle.fabric.registry;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.components.UuidComponent;
import io.github.flemmli97.mobbattle.components.UuidListComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModComponents {

    public static DataComponentType<AreaPositionComponent> BOX;
    public static DataComponentType<EffectComponent> EFFECT;
    public static DataComponentType<UuidComponent> SELECTED_MOB;
    public static DataComponentType<UuidListComponent> SELECTED_MOBS;

    public static void register() {
        BOX = register("box", new DataComponentType.Builder<AreaPositionComponent>().persistent(AreaPositionComponent.CODEC).networkSynchronized(AreaPositionComponent.STREAM_CODEC).build());
        EFFECT = register("effect", new DataComponentType.Builder<EffectComponent>().persistent(EffectComponent.CODEC).networkSynchronized(EffectComponent.STREAM_CODEC).build());
        SELECTED_MOB = register("selected_mob", new DataComponentType.Builder<UuidComponent>().persistent(UuidComponent.CODEC).networkSynchronized(UuidComponent.STREAM_CODEC).build());
        SELECTED_MOBS = register("selected_mobs", new DataComponentType.Builder<UuidListComponent>().persistent(UuidListComponent.CODEC).networkSynchronized(UuidListComponent.STREAM_CODEC).build());
    }

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, MobBattle.of(name), type);
    }
}
