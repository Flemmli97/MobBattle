package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class MobMount extends Item implements LeftClickInteractItem {

    public MobMount(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag b) {
        list.add(Component.translatable("tooltip.mount.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.mount.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(LibTags.SAVED_ENTITY);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide)
            if (stack.hasTag()) {
                stack.getTag().remove(LibTags.SAVED_ENTITY);
                player.sendSystemMessage(Component.translatable("tooltip.mount.reset").withStyle(ChatFormatting.RED));
            }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living instanceof Mob mob && !player.level().isClientSide) {
            if (stack.hasTag() && stack.getTag().contains(LibTags.SAVED_ENTITY)) {
                Mob storedEntity = Utils.fromUUID((ServerLevel) player.level(), stack.getTag().getString(LibTags.SAVED_ENTITY));
                if (storedEntity != null && storedEntity != mob && !this.passengerContainsEntity(storedEntity, mob)) {
                    storedEntity.startRiding(mob);
                    stack.getTag().remove(LibTags.SAVED_ENTITY);
                }
            } else {
                CompoundTag compound = new CompoundTag();
                if (stack.hasTag())
                    compound = stack.getTag();
                compound.putString(LibTags.SAVED_ENTITY, mob.getStringUUID());
                stack.setTag(compound);
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
