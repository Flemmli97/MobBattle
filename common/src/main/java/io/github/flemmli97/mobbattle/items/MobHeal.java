package io.github.flemmli97.mobbattle.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MobHeal extends Item implements LeftClickInteractItem {

    public MobHeal(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof Mob) {
            ((Mob) entity).heal(((Mob) entity).getMaxHealth() - ((Mob) entity).getHealth());
            entity.level.addParticle(ParticleTypes.HEART, entity.getX(), entity.getY() + entity.getBbHeight() + 0.5, entity.getZ(), 0, 0.1, 0);
        }
        return true;

    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> list, TooltipFlag flagIn) {
        list.add(new TranslatableComponent("tooltip.heal").withStyle(ChatFormatting.AQUA));
    }
}
