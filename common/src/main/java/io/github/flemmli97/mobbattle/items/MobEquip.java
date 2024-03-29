package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.handler.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MobEquip extends Item implements LeftClickInteractItem {

    public MobEquip(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag b) {
        list.add(Component.translatable("tooltip.equip.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.equip.second").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.equip.third").withStyle(ChatFormatting.AQUA));
    }

    public BlockPos[] getSelPos(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag compound = stack.getTag();
            BlockPos pos1 = null;
            if (compound.contains(LibTags.savedPos1) && compound.getIntArray(LibTags.savedPos1).length == 3)
                pos1 = new BlockPos(compound.getIntArray(LibTags.savedPos1)[0], compound.getIntArray(LibTags.savedPos1)[1], compound.getIntArray(LibTags.savedPos1)[2]);
            BlockPos pos2 = null;
            if (compound.contains(LibTags.savedPos2) && compound.getIntArray(LibTags.savedPos2).length == 3)
                pos2 = new BlockPos(compound.getIntArray(LibTags.savedPos2)[0], compound.getIntArray(LibTags.savedPos2)[1], compound.getIntArray(LibTags.savedPos2)[2]);
            return new BlockPos[]{pos1, pos2};
        }
        return new BlockPos[]{null, null};
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        if (!ctx.getLevel().isClientSide) {
            CompoundTag compound = stack.getTag();
            if (compound == null)
                compound = new CompoundTag();
            if (!compound.contains(LibTags.savedPos1) || compound.getIntArray(LibTags.savedPos1).length != 3) {
                compound.putIntArray(LibTags.savedPos1, new int[]{ctx.getClickedPos().getX(), ctx.getClickedPos().getY(), ctx.getClickedPos().getZ()});
            } else if (!ctx.getClickedPos().equals(
                    new BlockPos(compound.getIntArray(LibTags.savedPos1)[0], compound.getIntArray(LibTags.savedPos1)[1], compound.getIntArray(LibTags.savedPos1)[2]))) {
                compound.putIntArray(LibTags.savedPos2, new int[]{ctx.getClickedPos().getX(), ctx.getClickedPos().getY(), ctx.getClickedPos().getZ()});
            }
            stack.setTag(compound);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide && stack.hasTag())
            if (player.isShiftKeyDown()) {
                stack.getTag().remove(LibTags.savedPos1);
                stack.getTag().remove(LibTags.savedPos2);
                player.sendSystemMessage(Component.translatable("tooltip.equip.reset").withStyle(ChatFormatting.RED));
            } else if (stack.getTag().contains(LibTags.savedPos1) && stack.getTag().contains(LibTags.savedPos2)) {
                BlockPos pos1 = new BlockPos(stack.getTag().getIntArray(LibTags.savedPos1)[0], stack.getTag().getIntArray(LibTags.savedPos1)[1],
                        stack.getTag().getIntArray(LibTags.savedPos1)[2]);
                BlockPos pos2 = new BlockPos(stack.getTag().getIntArray(LibTags.savedPos2)[0], stack.getTag().getIntArray(LibTags.savedPos2)[1],
                        stack.getTag().getIntArray(LibTags.savedPos2)[2]);
                AABB bb = Utils.getBoundingBoxPositions(pos1, pos2);
                List<Mob> list = player.level.getEntitiesOfClass(Mob.class, bb);
                for (Mob living : list) {
                    living.addTag(LibTags.entityPickup);
                    CrossPlatformStuff.INSTANCE.goalSelectorFrom(living, false).addGoal(10, new EntityAIItemPickup(living));
                }
                player.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
            }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob && !player.level.isClientSide) {
            entity.addTag(LibTags.entityPickup);
            CrossPlatformStuff.INSTANCE.goalSelectorFrom((Mob) entity, false).addGoal(10, new EntityAIItemPickup((Mob) entity));
            player.sendSystemMessage(Component.translatable("tooltip.equip.add").withStyle(ChatFormatting.GOLD));
        }
        return true;
    }
}
