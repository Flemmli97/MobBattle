package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.common.components.EffectComponent;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MobEffectGive extends Item implements LeftClickInteractItem {

    public MobEffectGive(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("tooltip.effect.give.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.effect.give.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level.isClientSide)
            ClientHandler.openEffectGui();
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living != null && !player.level().isClientSide) {
            if (stack.has(CrossPlatformStuff.INSTANCE.getComponentEffect())) {
                EffectComponent effect = stack.get(CrossPlatformStuff.INSTANCE.getComponentEffect());
                effect.effect().ifPresent(eff -> {
                    living.addEffect(new MobEffectInstance(eff, effect.duration(), effect.amplifier(), false, effect.particles()));
                    player.sendSystemMessage(Component.translatable("tooltip.effect.give.add", Component.translatable(eff.value().getDescriptionId()), effect.amplifier(), effect.duration()).withStyle(ChatFormatting.GOLD));
                });
            }
        }
        return true;
    }
}
