package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.components.UuidComponent;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class MobStick extends Item implements LeftClickInteractItem {

    public MobStick(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag b) {
        UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
        if (comp != null && comp.name().isPresent()) {
            list.add(Component.translatable("tooltip.stick.contains", comp.name()).withStyle(ChatFormatting.GREEN));
        }
        list.add(Component.translatable("tooltip.stick.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.stick.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide) {
            UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            if (comp != null) {
                stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
                player.sendSystemMessage(Component.translatable("tooltip.stick.reset").withStyle(ChatFormatting.RED));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level() instanceof ServerLevel) {
            UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
            if (!(living instanceof Mob target))
                return true;
            if (comp != null && comp.uuid().isPresent()) {
                Mob storedEntity = Utils.fromUUID((ServerLevel) player.level(), comp.uuid().get());
                if (target != storedEntity) {
                    Utils.setAttackTarget(target, storedEntity, true);
                    stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
                    return true;
                }
            } else {
                stack.set(CrossPlatformStuff.INSTANCE.getComponentMobUuid(), new UuidComponent(Optional.of(target.getUUID()),
                        Optional.ofNullable(target.getCustomName())));
                player.sendSystemMessage(Component.translatable("tooltip.stick.add").withStyle(ChatFormatting.GOLD));
                return true;
            }
        }
        return true;
    }
}
