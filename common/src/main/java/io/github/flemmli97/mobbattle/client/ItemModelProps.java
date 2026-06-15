package io.github.flemmli97.mobbattle.client;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;

public class ItemModelProps {

    public static final ResourceLocation PERIMETER_REMOVE = MobBattle.of("perimeter_remove");

    public static final ClampedItemPropertyFunction PERIMETER_REMOVE_PROPERTY = (stack, world, entity, i) -> stack.has(MobBattleDataComponents.PERIMETER_REMOVE.get()) ? 1 : 0;
}
