package io.github.flemmli97.mobbattle.common.registry;

import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.common.components.EffectComponent;
import io.github.flemmli97.mobbattle.common.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.common.components.UuidComponent;
import io.github.flemmli97.mobbattle.common.components.UuidListComponent;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class MobBattleDataComponents {

    public static final Supplier<DataComponentType<AreaPositionComponent>> BOX = CrossPlatformStuff.INSTANCE.registerComponent("box",
            () -> new DataComponentType.Builder<AreaPositionComponent>().persistent(AreaPositionComponent.CODEC).networkSynchronized(AreaPositionComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<EffectComponent>> EFFECT = CrossPlatformStuff.INSTANCE.registerComponent("effect",
            () -> new DataComponentType.Builder<EffectComponent>().persistent(EffectComponent.CODEC).networkSynchronized(EffectComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<UuidComponent>> SELECTED_MOB = CrossPlatformStuff.INSTANCE.registerComponent("selected_mob",
            () -> new DataComponentType.Builder<UuidComponent>().persistent(UuidComponent.CODEC).networkSynchronized(UuidComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<UuidListComponent>> SELECTED_MOBS = CrossPlatformStuff.INSTANCE.registerComponent("selected_mobs",
            () -> new DataComponentType.Builder<UuidListComponent>().persistent(UuidListComponent.CODEC).networkSynchronized(UuidListComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<SpawnEggOptions>> SPAWN_EGG_OPTIONS = CrossPlatformStuff.INSTANCE.registerComponent("spawnegg_options",
            () -> new DataComponentType.Builder<SpawnEggOptions>().persistent(SpawnEggOptions.CODEC).networkSynchronized(SpawnEggOptions.STREAM_CODEC).build());

    public static void init() {
    }
}
