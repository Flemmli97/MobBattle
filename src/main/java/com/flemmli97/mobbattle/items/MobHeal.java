package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class MobHeal extends Item {

    public MobHeal() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity) {
            ((MobEntity) entity).heal(((MobEntity) entity).getMaxHealth() - ((MobEntity) entity).getHealth());
            entity.world.addParticle(ParticleTypes.HEART, entity.getPosX(), entity.getPosY() + entity.getHeight() + 0.5, entity.getPosZ(), 0, 0.1, 0);
        }
        return true;

    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        list.add(new TranslationTextComponent("tooltip.heal").mergeStyle(TextFormatting.AQUA));
    }
}
