package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.client.ClientOpenGuiHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MobEffectGive extends Item {

    public MobEffectGive() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        list.add(new StringTextComponent(TextFormatting.AQUA + "Right click to edit potion effect"));
        list.add(new StringTextComponent(TextFormatting.AQUA + "Left click an entity to add the potion effects"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND)
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientOpenGuiHelper::openEffectGUI);
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof LivingEntity && !player.world.isRemote) {
            LivingEntity e = (LivingEntity) entity;
            if (stack.hasTag()) {
                CompoundNBT compound = stack.getTag();
                String potionString = compound.getString(MobBattle.MODID + ":potion");
                int duration = compound.getInt(MobBattle.MODID + ":duration");
                int amplifier = compound.getInt(MobBattle.MODID + ":amplifier");
                boolean showEffect = compound.getBoolean(MobBattle.MODID + ":show");
                Effect potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionString));
                if (potion != null) {
                    e.addPotionEffect(new EffectInstance(potion, duration, amplifier, false, showEffect));
                    player.sendMessage(new StringTextComponent(
                            TextFormatting.GOLD + "Added effect " + potionString + " " + amplifier + " for " + duration + " ticks "), player.getUniqueID());
                }
            }
        }
        return true;
    }
}
