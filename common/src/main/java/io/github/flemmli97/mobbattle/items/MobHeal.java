package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class MobHeal extends Item implements LeftClickInteractItem {

    public MobHeal(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.heal").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living != null) {
            living.heal(living.getMaxHealth());
            entity.level().addParticle(ParticleTypes.HEART, entity.getX(), entity.getY() + entity.getBbHeight() + 0.5, entity.getZ(), 0, 0.1, 0);
        }
        return true;

    }
}
