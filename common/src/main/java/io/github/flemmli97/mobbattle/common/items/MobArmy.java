package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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

public class MobArmy extends Item implements LeftClickInteractItem {

    public MobArmy(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.army.first").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.army.second").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.army.third").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.army.forth").withStyle(ChatFormatting.AQUA));
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
                comp = comp.withSecond(ctx.getClickedPos());
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
                serverPlayer.sendSystemMessage(Component.translatable("tooltip.army.reset").withStyle(ChatFormatting.RED));
            } else {
                AreaPositionComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
                if (comp.first() != null && comp.second() != null) {
                    AABB bb = Utils.getBoundingBoxPositions(comp.first(), comp.second());
                    List<Mob> list = player.level().getEntitiesOfClass(Mob.class, bb);
                    String team = stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName().getString() : "DEFAULT";
                    for (Mob living : list) {
                        Utils.updateEntity(team, living);
                    }
                    serverPlayer.sendSystemMessage(Component.translatable("tooltip.army.add.box", team).withStyle(ChatFormatting.GOLD));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob mob && player instanceof ServerPlayer serverPlayer) {
            String team = stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName().getString() : "DEFAULT";
            Utils.updateEntity(team, mob);
            serverPlayer.sendSystemMessage(Component.translatable("tooltip.army.add", team).withStyle(ChatFormatting.GOLD));
        }
        return true;
    }
}
