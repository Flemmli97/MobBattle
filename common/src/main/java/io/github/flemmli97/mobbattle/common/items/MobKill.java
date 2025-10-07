package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class MobKill extends Item implements LeftClickInteractItem {

    public MobKill(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player instanceof ServerPlayer) {
            Entity target = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
            if (target != null) {
                target.hurt(entity.damageSources().genericKill(), Float.MAX_VALUE);
                if (target.isAlive()) {
                    target.kill();
                }
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("tooltip.kill").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public Entity getDefaultHover(EntityHitResult result) {
        return CrossPlatformStuff.INSTANCE.tryGetEntity(result.getEntity());
    }
}
