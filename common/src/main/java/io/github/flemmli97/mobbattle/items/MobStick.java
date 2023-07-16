package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MobStick extends Item implements LeftClickInteractItem {

    public MobStick(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag b) {
        if (stack.hasTag() && stack.getTag().contains(LibTags.savedEntityName)) {
            list.add(Component.translatable("tooltip.stick.contains", stack.getTag().getString(LibTags.savedEntityName)).withStyle(ChatFormatting.GREEN));
        }
        list.add(Component.translatable("tooltip.stick.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.stick.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level().isClientSide)
            if (stack.hasTag()) {
                stack.getTag().remove(LibTags.savedEntity);
                stack.getTag().remove(LibTags.savedEntityName);
                player.sendSystemMessage(Component.translatable("tooltip.stick.reset").withStyle(ChatFormatting.RED));
            }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level() instanceof ServerLevel)
            if (stack.hasTag() && stack.getTag().contains(LibTags.savedEntity)) {
                Mob storedEntity = Utils.fromUUID((ServerLevel) player.level(), stack.getTag().getString(LibTags.savedEntity));
                if (entity instanceof Mob living && entity != storedEntity) {
                    Utils.setAttackTarget(living, storedEntity, true);
                    stack.getTag().remove(LibTags.savedEntity);
                    stack.getTag().remove(LibTags.savedEntityName);
                    return true;
                }
            } else if (entity instanceof Mob) {
                CompoundTag compound = new CompoundTag();
                if (stack.hasTag())
                    compound = stack.getTag();
                compound.putString(LibTags.savedEntity, entity.getStringUUID());
                compound.putString(LibTags.savedEntityName, entity.getClass().getSimpleName());
                stack.setTag(compound);
                player.sendSystemMessage(Component.translatable("tooltip.stick.add").withStyle(ChatFormatting.GOLD));
                return true;
            }
        return true;
    }
}
