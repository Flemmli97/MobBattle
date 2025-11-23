package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ExtendedItem extends MobHighlightItem {

    String KEY_ID = MobBattle.MODID + ".key.item";

    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity);

    default boolean onFunctionPress(ItemStack stack, Player player) {
        return false;
    }
}
