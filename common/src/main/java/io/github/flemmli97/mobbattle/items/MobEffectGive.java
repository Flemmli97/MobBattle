package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class MobEffectGive extends Item implements LeftClickInteractItem {

    public MobEffectGive(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.effect.give.first").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.effect.give.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && level.isClientSide)
            ClientHandler.openEffectGui();
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living != null && player instanceof ServerPlayer serverPlayer) {
            if (stack.has(CrossPlatformStuff.INSTANCE.getComponentEffect())) {
                EffectComponent effect = stack.get(CrossPlatformStuff.INSTANCE.getComponentEffect());
                effect.effect().ifPresent(eff -> {
                    living.addEffect(new MobEffectInstance(eff, effect.duration(), effect.amplifier(), false, effect.particles()));
                    serverPlayer.sendSystemMessage(Component.translatable("tooltip.effect.give.add", Component.translatable(eff.value().getDescriptionId()), effect.amplifier(), effect.duration()).withStyle(ChatFormatting.GOLD));
                });
            }
        }
        return true;
    }
}
