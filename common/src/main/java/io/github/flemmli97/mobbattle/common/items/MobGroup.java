package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.UuidListComponent;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MobGroup extends Item implements LeftClickInteractItem, MobHighlightItem {

    public MobGroup(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag b) {
        list.add(Component.translatable("tooltip.group.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.group.second").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.group.third").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob mob && !player.isShiftKeyDown() && !player.level().isClientSide && stack.has(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid())) {
            UuidListComponent ids = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid());
            for (UUID id : ids.uuids()) {
                Mob e = Utils.fromUUID((ServerLevel) player.level(), id);
                if (mob != e) {
                    Utils.setAttackTarget(mob, e, true);
                }
            }
            stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid());
            player.setItemInHand(hand, stack);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide) {
            UuidListComponent ids = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid());
            if (ids != null && !ids.uuids().isEmpty()) {
                if (!player.isShiftKeyDown() && !ids.uuids().isEmpty()) {
                    ids = ids.update(List::removeLast);
                    stack.set(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid(), ids);
                    player.sendSystemMessage(Component.translatable("tooltip.group.remove").withStyle(ChatFormatting.RED));
                } else {
                    stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid());
                    player.sendSystemMessage(Component.translatable("tooltip.group.reset").withStyle(ChatFormatting.RED));
                }
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob && !player.level().isClientSide) {
            UuidListComponent ids = stack.getOrDefault(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid(), UuidListComponent.EMPTY);
            AtomicBoolean changed = new AtomicBoolean();
            ids = ids.update(list -> {
                if (!list.contains(living.getUUID())) {
                    list.add(living.getUUID());
                    changed.set(true);
                }
            });
            if (changed.get()) {
                stack.set(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid(), ids);
                player.sendSystemMessage(Component.translatable("tooltip.group.add").withStyle(ChatFormatting.GOLD));
            }
        }
        return true;
    }
}
