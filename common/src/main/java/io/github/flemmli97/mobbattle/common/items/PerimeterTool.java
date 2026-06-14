package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.PerimeterComponent;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.PerimeterData;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Consumer;

public class PerimeterTool extends Item implements ExtendedItem {

    public PerimeterTool(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.mobbattle.perimeter.1").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mobbattle.perimeter.2").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mobbattle.perimeter.3").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mobbattle.perimeter.mode", Component.keybind(ExtendedItem.KEY_ID)
                .withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.AQUA));
        if (stack.has(MobBattleDataComponents.PERIMETER_REMOVE.get())) {
            adder.accept(Component.translatable("tooltip.mobbattle.perimeter.remove").withStyle(ChatFormatting.AQUA));
        } else {
            adder.accept(Component.translatable("tooltip.mobbattle.perimeter.add").withStyle(ChatFormatting.AQUA));
            PerimeterComponent comp = stack.getOrDefault(MobBattleDataComponents.PERIMETER.get(), PerimeterComponent.DEFAULT);
            adder.accept(Component.translatable("tooltip.mobbattle.perimeter.setting", Component.translatable(comp.shape().translationKey), comp.inner(), comp.outer()).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return false;
    }

    @Override
    public Entity getDefaultHover(EntityHitResult result) {
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        if (ctx.getLevel() instanceof ServerLevel level) {
            if (stack.has(MobBattleDataComponents.PERIMETER_REMOVE.get())) {
                PerimeterData.get(level).setPerimeter(level, null);
            } else {
                PerimeterComponent comp = stack.getOrDefault(MobBattleDataComponents.PERIMETER.get(), PerimeterComponent.DEFAULT);
                BlockPos pos = ctx.getClickedPos().above();
                PerimeterData.get(level).setPerimeter(level, comp.toPerimeter(pos));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.has(MobBattleDataComponents.PERIMETER_REMOVE.get())) {
                PerimeterData.get(serverPlayer.level()).setPerimeter(serverPlayer.level(), null);
            } else {
                CrossPlatformStuff.INSTANCE.sendToClient(new S2CSpawnEggScreen(hand, S2CSpawnEggScreen.ScreenType.PERIMETER), serverPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onFunctionPress(ItemStack stack, Player player) {
        if (player.level().isClientSide())
            return true;
        if (stack.has(MobBattleDataComponents.PERIMETER_REMOVE.get())) {
            stack.remove(MobBattleDataComponents.PERIMETER_REMOVE.get());
        } else {
            stack.set(MobBattleDataComponents.PERIMETER_REMOVE.get(), Unit.INSTANCE);
        }
        return true;
    }
}
