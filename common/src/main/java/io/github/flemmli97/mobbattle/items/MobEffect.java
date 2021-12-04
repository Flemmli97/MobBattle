package io.github.flemmli97.mobbattle.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MobEffect extends Item implements LeftClickInteractItem {

    public MobEffect(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> list, TooltipFlag flagIn) {
        list.add(new TranslatableComponent("tooltip.effect.remove").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level.isClientSide && entity instanceof LivingEntity) {
            LivingEntity e = (LivingEntity) entity;
            //Clear Potion effects
            e.removeAllEffects();
            player.sendMessage(new TranslatableComponent("tooltip.effect.remove.clear").withStyle(ChatFormatting.GOLD), player.getUUID());
        }
        return true;
    }
}