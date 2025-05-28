package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.components.UuidComponent;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Consumer;

public class MobMount extends Item implements LeftClickInteractItem {

    public MobMount(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.mount.first").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mount.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
        return comp != null && comp.uuid().isPresent();
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            if (comp != null) {
                stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
                serverPlayer.sendSystemMessage(Component.translatable("tooltip.mount.reset").withStyle(ChatFormatting.RED));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob && !player.level().isClientSide) {
            UuidComponent comp = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            if (comp != null && comp.uuid().isPresent()) {
                Mob storedEntity = Utils.fromUUID((ServerLevel) player.level(), comp.uuid().get());
                if (storedEntity != null && storedEntity != entity && !this.passengerContainsEntity(storedEntity, entity)) {
                    storedEntity.startRiding(entity);
                    stack.remove(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
                }
            } else {
                stack.set(CrossPlatformStuff.INSTANCE.getComponentMobUuid(), new UuidComponent(Optional.of(entity.getUUID()),
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
