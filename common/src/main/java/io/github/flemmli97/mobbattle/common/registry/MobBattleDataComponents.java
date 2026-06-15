package io.github.flemmli97.mobbattle.common.registry;

import com.mojang.serialization.Codec;
import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.common.components.EffectComponent;
import io.github.flemmli97.mobbattle.common.components.PerimeterComponent;
import io.github.flemmli97.mobbattle.common.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.common.components.UuidComponent;
import io.github.flemmli97.mobbattle.common.components.UuidListComponent;
import io.github.flemmli97.mobbattle.common.items.MobKill;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;

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
    public static final Supplier<DataComponentType<MobKill.Mode>> KILL_MODE = CrossPlatformStuff.INSTANCE.registerComponent("kill_mode",
            () -> new DataComponentType.Builder<MobKill.Mode>().persistent(Codec.stringResolver(MobKill.Mode::toString, MobKill.Mode::valueOf))
                    .networkSynchronized(ByteBufCodecs.idMapper(i -> MobKill.Mode.values()[i], MobKill.Mode::ordinal)).build());
    public static final Supplier<DataComponentType<BossEvent.BossBarColor>> BOSS_BAR_COLOR = CrossPlatformStuff.INSTANCE.registerComponent("boss_bar_color",
            () -> new DataComponentType.Builder<BossEvent.BossBarColor>().persistent(Codec.stringResolver(BossEvent.BossBarColor::toString, BossEvent.BossBarColor::valueOf))
                    .networkSynchronized(ByteBufCodecs.idMapper(i -> BossEvent.BossBarColor.values()[i], BossEvent.BossBarColor::ordinal)).build());
    public static final Supplier<DataComponentType<PerimeterComponent>> PERIMETER = CrossPlatformStuff.INSTANCE.registerComponent("perimeter",
            () -> new DataComponentType.Builder<PerimeterComponent>().persistent(PerimeterComponent.CODEC).networkSynchronized(PerimeterComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<Unit>> PERIMETER_REMOVE = CrossPlatformStuff.INSTANCE.registerComponent("perimeter_remove",
            () -> new DataComponentType.Builder<Unit>().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build());

    public static void init() {
    }
}
