package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class MobArmor extends Item implements LeftClickInteractItem {

    public MobArmor(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player) || !player.getAbilities().instabuild;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.armor").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(target);
        if (living instanceof Mob mob && player instanceof ServerPlayer serverPlayer) {
            CrossPlatformStuff.INSTANCE.openGuiArmor(serverPlayer, mob);
        }
        return InteractionResult.SUCCESS;
    }
}
