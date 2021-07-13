package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.handler.LibTags;
import com.flemmli97.mobbattle.handler.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class MobGroup extends Item {

    public MobGroup() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<ITextComponent> list, ITooltipFlag b) {
        list.add(new StringTextComponent(TextFormatting.AQUA + "tooltip.group.first"));
        list.add(new StringTextComponent(TextFormatting.AQUA + "tooltip.group.second"));
        list.add(new StringTextComponent(TextFormatting.AQUA + "tooltip.group.third"));
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.isSneaking() && !player.world.isRemote && stack.hasTag() && stack.getTag().contains(LibTags.savedEntityList)) {
            ListNBT list = stack.getTag().getList(LibTags.savedEntityList, 8);
            for (int i = 0; i < list.size(); i++) {
                MobEntity e = Utils.fromUUID((ServerWorld) player.world, list.getString(i));
                if (entity instanceof MobEntity && entity != e) {
                    MobEntity living = (MobEntity) entity;
                    Utils.setAttackTarget(living, e, true);
                }
            }
            stack.getTag().remove(LibTags.savedEntityList);
            player.setHeldItem(hand, stack);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote && stack.hasTag() && stack.getTag().contains(LibTags.savedEntityList)) {
            if (!player.isSneaking() && stack.getTag().getList(LibTags.savedEntityList, 8).size() > 0) {
                ListNBT list = stack.getTag().getList(LibTags.savedEntityList, 8);
                list.remove(list.size() - 1);
                stack.getTag().put(LibTags.savedEntityList, list);
                player.sendMessage(new TranslationTextComponent("tooltip.group.remove").mergeStyle(TextFormatting.RED), player.getUniqueID());
            } else {
                stack.getTag().remove(LibTags.savedEntityList);
                player.sendMessage(new TranslationTextComponent("tooltip.group.reset").mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity && !player.world.isRemote) {
            CompoundNBT compound = new CompoundNBT();
            if (stack.hasTag())
                compound = stack.getTag();
            ArrayList<String> list = new ArrayList<>();

            if (compound.contains(LibTags.savedEntityList)) {
                for (int i = 0; i < compound.getList(LibTags.savedEntityList, 8).size(); i++) {
                    list.add(compound.getList(LibTags.savedEntityList, 8).getString(i));
                }
            }
            if (!list.contains(entity.getCachedUniqueIdString())) {
                ListNBT nbttaglist = new ListNBT();
                if (compound.contains(LibTags.savedEntityList))
                    nbttaglist = compound.getList(LibTags.savedEntityList, 8);
                nbttaglist.add(StringNBT.valueOf(entity.getCachedUniqueIdString()));
                compound.put(LibTags.savedEntityList, nbttaglist);
                stack.setTag(compound);
                player.sendMessage(new TranslationTextComponent("tooltip.group.add").mergeStyle(TextFormatting.GOLD), player.getUniqueID());
            }
        }
        return true;
    }
}
