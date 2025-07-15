package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.common.entity.ai.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.common.utils.LibTags;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

public class MobEquip extends Item implements LeftClickInteractItem {

    public MobEquip(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.equip.first").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.equip.second").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.equip.third").withStyle(ChatFormatting.AQUA));
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
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer && stack.has(CrossPlatformStuff.INSTANCE.getComponentAreaSelection())) {
            if (player.isShiftKeyDown()) {
                stack.remove(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
                serverPlayer.sendSystemMessage(Component.translatable("tooltip.equip.reset").withStyle(ChatFormatting.RED));
            } else {
                AreaPositionComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
                if (comp.first() != null && comp.second() != null) {
                    AABB bb = Utils.getBoundingBoxPositions(comp.first(), comp.second());
                    List<Mob> list = player.level().getEntitiesOfClass(Mob.class, bb);
                    for (Mob living : list) {
                        living.addTag(LibTags.ENTITY_PICKUP);
                        CrossPlatformStuff.INSTANCE.goalSelectorFrom(living, false).addGoal(10, new EntityAIItemPickup(living));
                    }
                    serverPlayer.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob mob && player instanceof ServerPlayer serverPlayer) {
            mob.addTag(LibTags.ENTITY_PICKUP);
            CrossPlatformStuff.INSTANCE.goalSelectorFrom(mob, false).addGoal(10, new EntityAIItemPickup(mob));
            serverPlayer.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
        }
        return true;
    }
}
