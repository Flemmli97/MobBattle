package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

public class MobKill extends Item implements ExtendedItem {

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
        list.add(Component.translatable("tooltip.mobbattle.kill").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.mobbattle.kill.mode", Component.translatable(stack.getOrDefault(MobBattleDataComponents.KILL_MODE.get(), Mode.SINGLE).translationKey)
                        .withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.mobbattle.kill.mode.switch", Component.keybind(ExtendedItem.KEY_ID)
                .withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            Mode mode = stack.getOrDefault(MobBattleDataComponents.KILL_MODE.get(), Mode.SINGLE);
            if (mode == Mode.ALL) {
                serverLevel.getEntities(EntityTypeTest.forClass(Entity.class), e -> !(e instanceof Player))
                        .forEach(target -> {
                            target.hurt(target.damageSources().genericKill(), Float.MAX_VALUE);
                            if (target.isAlive()) {
                                target.kill();
                            }
                        });
                player.sendSystemMessage(Component.translatable("tooltip.mobbattle.kill.all.success").withStyle(ChatFormatting.RED));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public Entity getDefaultHover(EntityHitResult result) {
        return CrossPlatformStuff.INSTANCE.tryGetEntity(result.getEntity());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(MobBattleDataComponents.KILL_MODE.get(), Mode.SINGLE) == Mode.ALL;
    }

    @Override
    public boolean onFunctionPress(ItemStack stack, Player player) {
        if (player.level().isClientSide())
            return true;
        Mode mode = stack.getOrDefault(MobBattleDataComponents.KILL_MODE.get(), Mode.SINGLE);
        mode = mode == Mode.SINGLE ? Mode.ALL : Mode.SINGLE;
        stack.set(MobBattleDataComponents.KILL_MODE.get(), mode);
        return true;
    }

    public enum Mode {
        SINGLE("tooltip.mobbattle.kill.mode.single"),
        ALL("tooltip.mobbattle.kill.mode.all");

        public final String translationKey;

        Mode(String translationKey) {
            this.translationKey = translationKey;
        }
    }
}
