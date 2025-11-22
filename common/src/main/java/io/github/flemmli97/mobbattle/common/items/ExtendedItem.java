package io.github.flemmli97.mobbattle.common.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ExtendedItem extends MobHighlightItem {

    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity);
}
