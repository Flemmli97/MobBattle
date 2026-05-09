package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.UuidListComponent;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MobGroup extends Item implements ExtendedItem, MobHighlightItem {

    public MobGroup(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.mobbattle.group.first").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mobbattle.group.second").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mobbattle.group.third").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
        if (living instanceof Mob mob && !player.isShiftKeyDown() && !player.level().isClientSide() && stack.has(MobBattleDataComponents.SELECTED_MOBS.get())) {
            UuidListComponent ids = stack.get(MobBattleDataComponents.SELECTED_MOBS.get());
            for (UUID id : ids.uuids()) {
                Mob e = Utils.fromUUID((ServerLevel) player.level(), id);
                if (mob != e) {
                    Utils.setAttackTarget(mob, e, true);
                }
            }
            stack.remove(MobBattleDataComponents.SELECTED_MOBS.get());
            player.setItemInHand(hand, stack);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide()) {
            UuidListComponent ids = stack.get(MobBattleDataComponents.SELECTED_MOBS.get());
            if (ids != null && !ids.uuids().isEmpty()) {
                if (!player.isShiftKeyDown() && !ids.uuids().isEmpty()) {
                    ids = ids.update(List::removeLast);
                    stack.set(MobBattleDataComponents.SELECTED_MOBS.get(), ids);
                    player.sendSystemMessage(Component.translatable("tooltip.mobbattle.group.remove").withStyle(ChatFormatting.RED));
                } else {
                    stack.remove(MobBattleDataComponents.SELECTED_MOBS.get());
                    player.sendSystemMessage(Component.translatable("tooltip.mobbattle.group.reset").withStyle(ChatFormatting.RED));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
        if (living instanceof Mob && !player.level().isClientSide()) {
            UuidListComponent ids = stack.getOrDefault(MobBattleDataComponents.SELECTED_MOBS.get(), UuidListComponent.EMPTY);
            AtomicBoolean changed = new AtomicBoolean();
            ids = ids.update(list -> {
                if (!list.contains(living.getUUID())) {
                    list.add(living.getUUID());
                    changed.set(true);
                }
            });
            if (changed.get()) {
                stack.set(MobBattleDataComponents.SELECTED_MOBS.get(), ids);
                player.sendSystemMessage(Component.translatable("tooltip.mobbattle.group.add").withStyle(ChatFormatting.GOLD));
            }
        }
        return true;
    }
}
