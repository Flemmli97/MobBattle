package io.github.flemmli97.mobbattle.client;

import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.ItemStack;

public class BossBarItemColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1)
            return -1;
        BossEvent.BossBarColor color = stack.getOrDefault(MobBattleDataComponents.BOSS_BAR_COLOR.get(), BossEvent.BossBarColor.WHITE);
        return FastColor.ARGB32.color(255, color.getFormatting().getColor());
    }
}
