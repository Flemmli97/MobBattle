package io.github.flemmli97.mobbattle.items;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MobEffectGive extends Item implements LeftClickInteractItem {

    public MobEffectGive(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> list, TooltipFlag flagIn) {
        list.add(Component.translatable("tooltip.effect.give.first").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("tooltip.effect.give.second").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && world.isClientSide)
            ClientHandler.openEffectGui();
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        LivingEntity living = CrossPlatformStuff.INSTANCE.tryGetEntity(entity);
        if (living != null && !player.level().isClientSide) {
            if (stack.hasTag()) {
                CompoundTag compound = stack.getTag();
                String potionString = compound.getString(MobBattle.MODID + ":potion");
                int duration = compound.getInt(MobBattle.MODID + ":duration");
                int amplifier = compound.getInt(MobBattle.MODID + ":amplifier");
                boolean showEffect = compound.getBoolean(MobBattle.MODID + ":show");
                MobEffect potion = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(potionString));
                if (potion != null) {
                    living.addEffect(new MobEffectInstance(potion, duration, amplifier, false, showEffect));
                    player.sendSystemMessage(Component.translatable("tooltip.effect.give.add", Component.translatable(potion.getDescriptionId()), amplifier, duration).withStyle(ChatFormatting.GOLD));
                }
            }
        }
        return true;
    }

    public void updateFrom(ItemStack stack, String potion, int duration, int amplifier, boolean particle) {
        CompoundTag compound = stack.getTag();
        if (compound == null)
            compound = new CompoundTag();
        compound.putString(MobBattle.MODID + ":potion", potion);
        compound.putInt(MobBattle.MODID + ":duration", duration);
        compound.putInt(MobBattle.MODID + ":amplifier", amplifier);
        compound.putBoolean(MobBattle.MODID + ":show", particle);
        stack.setTag(compound);
    }

    public EffectData getData(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null)
            compound = new CompoundTag();
        return new EffectData(compound.getString(MobBattle.MODID + ":potion"), compound.getInt(MobBattle.MODID + ":duration"),
                compound.getInt(MobBattle.MODID + ":amplifier"), compound.getBoolean(MobBattle.MODID + ":show"));
    }

    public record EffectData(String potion, int duration, int amplifier, boolean particle) {
    }
}
