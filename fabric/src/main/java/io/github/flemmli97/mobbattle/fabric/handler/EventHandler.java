package io.github.flemmli97.mobbattle.fabric.handler;

import io.github.flemmli97.mobbattle.common.items.LeftClickInteractItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class EventHandler {

    public static InteractionResult attackCallback(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof LeftClickInteractItem) {
            if (((LeftClickInteractItem) stack.getItem()).onLeftClickEntity(stack, player, entity))
                return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
