package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MobHeal  extends Item{
	
	public MobHeal()
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_heal"));	
	}

	@Override
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) 
	{
		return !player.isCreative();
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if(entity instanceof MobEntity)
		{
			((MobEntity) entity).heal(((MobEntity) entity).getMaxHealth() - ((MobEntity) entity).getHealth());
			entity.world.addParticle(ParticleTypes.HEART, entity.posX, entity.posY + entity.getHeight() + 0.5, entity.posZ, 0, 0.1, 0);				
		}
		return true;
		
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
		list.add(new StringTextComponent(TextFormatting.AQUA + "Left click on entity to heal it"));
	}
}
