package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.BossbarHandler;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BossBarAdder extends Item implements ExtendedItem {

    public BossBarAdder(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("tooltip.mobbattle.bossbar").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.mobbattle.bossbar.remove").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.mobbattle.bossbar.color", Component.keybind(ExtendedItem.KEY_ID)
                .withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player instanceof ServerPlayer) {
            LivingEntity target = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
            if (target != null) {
                BossbarHandler.get(player.getServer()).addBossBarTo(target, stack.getOrDefault(MobBattleDataComponents.BOSS_BAR_COLOR.get(), BossEvent.BossBarColor.WHITE));
            }
        }
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand usedHand) {
        if (player instanceof ServerPlayer) {
            LivingEntity target = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
            if (target != null) {
                BossbarHandler.get(player.getServer()).removeBossbar(target);
            }
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    @Override
    public boolean onFunctionPress(ItemStack stack, Player player) {
        if (player.level().isClientSide())
            return true;
        BossEvent.BossBarColor color = stack.getOrDefault(MobBattleDataComponents.BOSS_BAR_COLOR.get(), BossEvent.BossBarColor.WHITE);
        color = BossEvent.BossBarColor.values()[(color.ordinal() + 1) % BossEvent.BossBarColor.values().length];
        stack.set(MobBattleDataComponents.BOSS_BAR_COLOR.get(), color);
        return true;
    }
}
