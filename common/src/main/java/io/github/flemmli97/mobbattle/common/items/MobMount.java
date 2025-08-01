package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.UuidComponent;
import io.github.flemmli97.mobbattle.common.utils.Utils;
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

public class MobMount extends Item implements LeftClickInteractItem {

    public MobMount(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag b) {
        list.add(Component.translatable("tooltip.mount.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.mount.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
        return comp != null && comp.uuid().isPresent();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide) {
            UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            if (comp != null) {
                stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
                player.sendSystemMessage(Component.translatable("tooltip.mount.reset").withStyle(ChatFormatting.RED));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob mob && !player.level().isClientSide) {
            UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            if (comp != null && comp.uuid().isPresent()) {
                Mob storedEntity = Utils.fromUUID((ServerLevel) player.level(), comp.uuid().get());
                if (storedEntity != null && storedEntity != mob && !this.passengerContainsEntity(storedEntity, mob)) {
                    storedEntity.startRiding(mob);
                    stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
                }
            } else {
                stack.set(CrossPlatformStuff.INSTANCE.getComponentMobUuid(), new UuidComponent(Optional.of(mob.getUUID()),
                        Optional.empty()));
            }
        }
        return true;
    }

    private boolean passengerContainsEntity(Entity theEntity, Entity entitySearch) {
        if (!theEntity.getPassengers().isEmpty())
            if (theEntity.getPassengers().contains(entitySearch))
                return true;
            else
                return this.passengerContainsEntity(theEntity.getPassengers().get(0), entitySearch);
        return false;
    }
}
