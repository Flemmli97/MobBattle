package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

import java.util.ArrayList;
import java.util.List;

public class MobGroup extends Item implements LeftClickInteractItem {

    public MobGroup(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level player, List<Component> list, TooltipFlag b) {
        list.add(Component.translatable("tooltip.group.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.group.second").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.group.third").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!player.isShiftKeyDown() && !player.level.isClientSide && stack.hasTag() && stack.getTag().contains(LibTags.savedEntityList)) {
            ListTag list = stack.getTag().getList(LibTags.savedEntityList, 8);
            for (int i = 0; i < list.size(); i++) {
                Mob e = Utils.fromUUID((ServerLevel) player.level, list.getString(i));
                if (entity instanceof Mob living && entity != e) {
                    Utils.setAttackTarget(living, e, true);
                }
            }
            stack.getTag().remove(LibTags.savedEntityList);
            player.setItemInHand(hand, stack);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.level.isClientSide && stack.hasTag() && stack.getTag().contains(LibTags.savedEntityList)) {
            if (!player.isShiftKeyDown() && stack.getTag().getList(LibTags.savedEntityList, 8).size() > 0) {
                ListTag list = stack.getTag().getList(LibTags.savedEntityList, 8);
                list.remove(list.size() - 1);
                stack.getTag().put(LibTags.savedEntityList, list);
                player.sendSystemMessage(Component.translatable("tooltip.group.remove").withStyle(ChatFormatting.RED));
            } else {
                stack.getTag().remove(LibTags.savedEntityList);
                player.sendSystemMessage(Component.translatable("tooltip.group.reset").withStyle(ChatFormatting.RED));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob && !player.level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            if (stack.hasTag())
                compound = stack.getTag();
            ArrayList<String> list = new ArrayList<>();

            if (compound.contains(LibTags.savedEntityList)) {
                for (int i = 0; i < compound.getList(LibTags.savedEntityList, 8).size(); i++) {
                    list.add(compound.getList(LibTags.savedEntityList, 8).getString(i));
                }
            }
            if (!list.contains(entity.getStringUUID())) {
                ListTag nbttaglist = new ListTag();
                if (compound.contains(LibTags.savedEntityList))
                    nbttaglist = compound.getList(LibTags.savedEntityList, 8);
                nbttaglist.add(StringTag.valueOf(entity.getStringUUID()));
                compound.put(LibTags.savedEntityList, nbttaglist);
                stack.setTag(compound);
                player.sendSystemMessage(Component.translatable("tooltip.group.add").withStyle(ChatFormatting.GOLD));
            }
        }
        return true;
    }
}
