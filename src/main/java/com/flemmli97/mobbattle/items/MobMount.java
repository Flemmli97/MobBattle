package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.handler.LibTags;
import com.flemmli97.mobbattle.handler.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class MobMount extends Item {

    public MobMount() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {
        list.add(new TranslationTextComponent("tooltip.mount.first").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.mount.second").mergeStyle(TextFormatting.AQUA));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(LibTags.savedEntity);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote)
            if (stack.hasTag()) {
                stack.getTag().remove(LibTags.savedEntity);
                player.sendMessage(new TranslationTextComponent("tooltip.mount.reset").mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity && !player.world.isRemote) {
            if (stack.hasTag() && stack.getTag().contains(LibTags.savedEntity)) {
                MobEntity storedEntity = Utils.fromUUID((ServerWorld) player.world, stack.getTag().getString(LibTags.savedEntity));
                if (storedEntity != null && storedEntity != entity && !this.passengerContainsEntity(storedEntity, entity)) {
                    storedEntity.startRiding(entity);
                    stack.getTag().remove(LibTags.savedEntity);
                }
            } else {
                CompoundNBT compound = new CompoundNBT();
                if (stack.hasTag())
                    compound = stack.getTag();
                compound.putString(LibTags.savedEntity, entity.getCachedUniqueIdString());
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
