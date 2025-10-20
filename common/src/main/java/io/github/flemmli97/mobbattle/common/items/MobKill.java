package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
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
        list.add(Component.translatable("tooltip.kill.all").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel) {
            player.startUsingItem(hand);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        int i = this.getUseDuration(stack, entity) - remainingUseDuration;
        if (i == 20 && entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSoundPacket(SoundEvents.NOTE_BLOCK_HARP, entity.getSoundSource(), entity.getX(), entity.getY(), entity.getZ(),
                    1, 2, 0));
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        int i = this.getUseDuration(stack, entity) - timeCharged;
        if (i > 20 && level instanceof ServerLevel serverLevel && entity instanceof Player player) {
            serverLevel.getEntities(EntityTypeTest.forClass(Entity.class), e -> !(e instanceof Player))
                    .forEach(target -> {
                        target.hurt(target.damageSources().genericKill(), Float.MAX_VALUE);
                        if (target.isAlive()) {
                            target.kill();
                        }
                    });
            player.sendSystemMessage(Component.translatable("tooltip.kill.all.success").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public Entity getDefaultHover(EntityHitResult result) {
        return CrossPlatformStuff.INSTANCE.tryGetEntity(result.getEntity());
    }
}
