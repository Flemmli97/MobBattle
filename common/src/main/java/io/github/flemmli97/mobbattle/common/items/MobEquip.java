package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.common.entity.ai.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.LibTags;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MobEquip extends Item implements ExtendedItem {

    public MobEquip(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag b) {
        list.add(Component.translatable("tooltip.equip.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.equip.second").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.equip.third").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        if (!ctx.getLevel().isClientSide) {
            AreaPositionComponent comp = stack.getOrDefault(MobBattleDataComponents.BOX.get(), AreaPositionComponent.DEFAULT);
            boolean update = false;
            if (comp.first() == null) {
                comp = comp.withFirst(ctx.getClickedPos());
                update = true;
            } else if (!ctx.getClickedPos().equals(comp.first())) {
                comp.withSecond(ctx.getClickedPos());
                update = true;
            }
            if (update)
                stack.set(MobBattleDataComponents.BOX.get(), comp);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && stack.has(MobBattleDataComponents.BOX.get())) {
            if (player.isShiftKeyDown()) {
                stack.remove(MobBattleDataComponents.BOX.get());
                player.sendSystemMessage(Component.translatable("tooltip.equip.reset").withStyle(ChatFormatting.RED));
            } else {
                AreaPositionComponent comp = stack.get(MobBattleDataComponents.BOX.get());
                if (comp.first() != null && comp.second() != null) {
                    AABB bb = Utils.getBoundingBoxPositions(comp.first(), comp.second());
                    List<Mob> list = player.level().getEntitiesOfClass(Mob.class, bb);
                    for (Mob living : list) {
                        living.addTag(LibTags.ENTITY_PICKUP);
                        CrossPlatformStuff.INSTANCE.goalSelectorFrom(living, false).addGoal(10, new EntityAIItemPickup(living));
                    }
                    player.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
                }
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
        if (living instanceof Mob mob && !player.level().isClientSide) {
            mob.addTag(LibTags.ENTITY_PICKUP);
            CrossPlatformStuff.INSTANCE.goalSelectorFrom(mob, false).addGoal(10, new EntityAIItemPickup(mob));
            player.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
        }
        return true;
    }
}
