package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.handler.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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

public class MobEquip extends Item implements LeftClickInteractItem {

    public MobEquip(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
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
            AreaPositionComponent comp = stack.getOrDefault(CrossPlatformStuff.INSTANCE.getComponentAreaSelection(), AreaPositionComponent.DEFAULT);
            boolean update = false;
            if (comp.first() == null) {
                comp = comp.withFirst(ctx.getClickedPos());
                update = true;
            } else if (!ctx.getClickedPos().equals(comp.first())) {
                comp.withSecond(ctx.getClickedPos());
                update = true;
            }
            if (update)
                stack.set(CrossPlatformStuff.INSTANCE.getComponentAreaSelection(), comp);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide && stack.has(CrossPlatformStuff.INSTANCE.getComponentAreaSelection())) {
            if (player.isShiftKeyDown()) {
                stack.remove(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
                player.sendSystemMessage(Component.translatable("tooltip.equip.reset").withStyle(ChatFormatting.RED));
            } else {
                AreaPositionComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
                if (comp.first() != null && comp.second() != null) {
                    AABB bb = Utils.getBoundingBoxPositions(comp.first(), comp.second());
                    List<Mob> list = player.level().getEntitiesOfClass(Mob.class, bb);
                    for (Mob living : list) {
                        living.addTag(LibTags.entityPickup);
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
        if (entity instanceof Mob && !player.level().isClientSide) {
            entity.addTag(LibTags.entityPickup);
            CrossPlatformStuff.INSTANCE.goalSelectorFrom((Mob) entity, false).addGoal(10, new EntityAIItemPickup((Mob) entity));
            player.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
        }
        return true;
    }
}
