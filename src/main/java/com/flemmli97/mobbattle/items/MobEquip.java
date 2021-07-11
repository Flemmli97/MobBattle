package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.items.entitymanager.EntityAIItemPickup;
import com.flemmli97.mobbattle.items.entitymanager.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class MobEquip extends Item {

    public MobEquip() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {
        list.add(new TranslationTextComponent("tooltip.equip.first").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.equip.second").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.equip.third").mergeStyle(TextFormatting.AQUA));
    }

    public BlockPos[] getSelPos(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
            BlockPos pos1 = null;
            if (compound.contains("Position1") && compound.getIntArray("Position1").length == 3)
                pos1 = new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2]);
            BlockPos pos2 = null;
            if (compound.contains("Position2") && compound.getIntArray("Position2").length == 3)
                pos2 = new BlockPos(compound.getIntArray("Position2")[0], compound.getIntArray("Position2")[1], compound.getIntArray("Position2")[2]);
            return new BlockPos[]{pos1, pos2};
        }
        return new BlockPos[]{null, null};
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getItem();
        if (!ctx.getWorld().isRemote) {
            CompoundNBT compound = stack.getTag();
            if (compound == null)
                compound = new CompoundNBT();
            if (!compound.contains("Position1") || compound.getIntArray("Position1").length != 3) {
                compound.putIntArray("Position1", new int[]{ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ()});
            } else if (!ctx.getPos().equals(
                    new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2]))) {
                compound.putIntArray("Position2", new int[]{ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ()});
            }
            stack.setTag(compound);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && stack.hasTag())
            if (player.isSneaking()) {
                stack.getTag().remove("Position1");
                stack.getTag().remove("Position2");
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Reset Positions"), player.getUniqueID());
            } else if (stack.getTag().contains("Position1") && stack.getTag().contains("Position2")) {
                BlockPos pos1 = new BlockPos(stack.getTag().getIntArray("Position1")[0], stack.getTag().getIntArray("Position1")[1],
                        stack.getTag().getIntArray("Position1")[2]);
                BlockPos pos2 = new BlockPos(stack.getTag().getIntArray("Position2")[0], stack.getTag().getIntArray("Position2")[1],
                        stack.getTag().getIntArray("Position2")[2]);
                AxisAlignedBB bb = Utils.getBoundingBoxPositions(pos1, pos2);
                List<MobEntity> list = player.world.getEntitiesWithinAABB(MobEntity.class, bb);
                for (MobEntity living : list) {
                    living.addTag("PickUp");
                    living.goalSelector.addGoal(10, new EntityAIItemPickup(living));
                }
                player.sendMessage(new TranslationTextComponent("tooltip.equip.add").mergeStyle(TextFormatting.GOLD), player.getUniqueID());
            }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity && !player.world.isRemote) {
            entity.addTag("PickUp");
            ((MobEntity) entity).goalSelector.addGoal(10, new EntityAIItemPickup((MobEntity) entity));
            player.sendMessage(new StringTextComponent(TextFormatting.GOLD + "Entity can pickup items now"), player.getUniqueID());
        }
        return true;
    }
}
