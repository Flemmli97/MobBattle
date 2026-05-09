package io.github.flemmli97.mobbattle.common.items;

import io.github.flemmli97.mobbattle.common.components.UuidComponent;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

public class MobMount extends Item implements ExtendedItem {

    public MobMount(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.mobbattle.mount.first").withStyle(ChatFormatting.AQUA));
        adder.accept(Component.translatable("tooltip.mobbattle.mount.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        UuidComponent comp = stack.get(MobBattleDataComponents.SELECTED_MOB.get());
        return comp != null && comp.uuid().isPresent();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide()) {
            UuidComponent comp = stack.get(MobBattleDataComponents.SELECTED_MOB.get());
            if (comp != null) {
                stack.remove(MobBattleDataComponents.SELECTED_MOB.get());
                player.sendSystemMessage(Component.translatable("tooltip.mobbattle.mount.reset").withStyle(ChatFormatting.RED));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetLivingEntity(entity);
        if (living instanceof Mob mob && !player.level().isClientSide()) {
            UuidComponent comp = stack.get(MobBattleDataComponents.SELECTED_MOB.get());
            if (comp != null && comp.uuid().isPresent()) {
                Mob storedEntity = Utils.fromUUID((ServerLevel) player.level(), comp.uuid().get());
                if (storedEntity != null && storedEntity != mob && !this.passengerContainsEntity(storedEntity, mob)) {
                    storedEntity.startRiding(mob);
                    stack.remove(MobBattleDataComponents.SELECTED_MOB.get());
                }
            } else {
                stack.set(MobBattleDataComponents.SELECTED_MOB.get(), new UuidComponent(Optional.of(mob.getUUID()),
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
