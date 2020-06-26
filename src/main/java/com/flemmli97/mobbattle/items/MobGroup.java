package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.items.entitymanager.Team;
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
        list.add(new StringTextComponent(TextFormatting.AQUA + "Left click to select entities"));
        list.add(new StringTextComponent(TextFormatting.AQUA + "Right click on entity to set the target"));
        list.add(new StringTextComponent(TextFormatting.AQUA + "Shift right click to reset"));
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.isSneaking() && !player.world.isRemote && stack.hasTag() && stack.getTag().contains("EntityList")) {
            ListNBT list = stack.getTag().getList("EntityList", 8);
            for (int i = 0; i < list.size(); i++) {
                MobEntity e = Team.fromUUID((ServerWorld) player.world, list.getString(i));
                if (entity != e && e != null) {
                    MobEntity living = (MobEntity) entity;
                    living.setAttackTarget(e);
                    e.setAttackTarget(living);
                }
            }
            stack.getTag().remove("EntityList");
            player.setHeldItem(hand, stack);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote && stack.hasTag() && stack.getTag().contains("EntityList")) {
            if (!player.isSneaking() && stack.getTag().getList("EntityList", 8).size() > 0) {
                ListNBT list = stack.getTag().getList("EntityList", 8);
                list.remove(list.size() - 1);
                stack.getTag().put("EntityList", list);
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Removed an entity"), player.getUniqueID());
            } else {
                stack.getTag().remove("EntityList");
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Reset all entities"), player.getUniqueID());
            }
        }
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity && !player.world.isRemote) {
            CompoundNBT compound = new CompoundNBT();
            if (stack.hasTag())
                compound = stack.getTag();
            ArrayList<String> list = new ArrayList<String>();

            if (compound.contains("EntityList")) {
                for (int i = 0; i < compound.getList("EntityList", 8).size(); i++) {
                    list.add(compound.getList("EntityList", 8).getString(i));
                }
            }
            if (!list.contains(entity.getCachedUniqueIdString())) {
                ListNBT nbttaglist = new ListNBT();
                if (compound.contains("EntityList"))
                    nbttaglist = compound.getList("EntityList", 8);
                nbttaglist.add(StringNBT.valueOf(entity.getCachedUniqueIdString()));
                compound.put("EntityList", nbttaglist);
                stack.setTag(compound);
                player.sendMessage(new StringTextComponent(TextFormatting.GOLD + "Added an entity"), player.getUniqueID());
            }
        }
        return true;
    }
}
