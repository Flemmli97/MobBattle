package io.github.flemmli97.mobbattle.client;

import com.mojang.serialization.MapCodec;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class BossBarItemColor implements ItemTintSource {

    public static final Identifier ID = MobBattle.of("boss_bar_color");

    public static final BossBarItemColor INSTANCE = new BossBarItemColor();

    public static final MapCodec<BossBarItemColor> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        BossEvent.BossBarColor color = itemStack.getOrDefault(MobBattleDataComponents.BOSS_BAR_COLOR.get(), BossEvent.BossBarColor.WHITE);
        return ARGB.color(255, color.getFormatting().getColor());
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
