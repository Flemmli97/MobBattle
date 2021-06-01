package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.items.entitymanager.Utils;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class MobStick extends Item {

    public MobStick() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {
        if (stack.hasTag() && stack.getTag().contains("StoredEntityName")) {
            list.add(new StringTextComponent(TextFormatting.GREEN + "Asigned entity: " + stack.getTag().getString("StoredEntityName")));
        }
        list.add(new StringTextComponent(TextFormatting.AQUA + "Left click to asign an entity"));
        list.add(new StringTextComponent(TextFormatting.AQUA + "Right click to reset"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote)
            if (stack.hasTag()) {
                stack.getTag().remove("StoredEntity");
                stack.getTag().remove("StoredEntityName");
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Reset entities"), player.getUniqueID());
            }
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.world.isRemote)
            if (stack.hasTag() && stack.getTag().contains("StoredEntity")) {
                MobEntity storedEntity = Utils.fromUUID((ServerWorld) player.world, stack.getTag().getString("StoredEntity"));
                if (entity instanceof MobEntity && entity != storedEntity) {
                    MobEntity living = (MobEntity) entity;
                    Utils.setAttackTarget(living, storedEntity, true);
                    stack.getTag().remove("StoredEntity");
                    stack.getTag().remove("StoredEntityName");
                    return true;
                }
            } else if (entity instanceof MobEntity) {
                CompoundNBT compound = new CompoundNBT();
                if (stack.hasTag())
                    compound = stack.getTag();
                compound.putString("StoredEntity", entity.getCachedUniqueIdString());
                compound.putString("StoredEntityName", entity.getClass().getSimpleName());
                stack.setTag(compound);
                player.sendMessage(new StringTextComponent(TextFormatting.GOLD + "First entity set, hit another entity to set target"), player.getUniqueID());
                return true;
            }
        return true;
    }
}
