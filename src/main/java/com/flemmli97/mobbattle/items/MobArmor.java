package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.inv.ContainerArmor;
import com.flemmli97.mobbattle.network.PacketHandler;
import com.flemmli97.mobbattle.network.PacketOpenGuiArmor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MobArmor extends Item{

	public MobArmor() 
	{
		super(new Item.Properties().maxStackSize(1).group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_armor"));	
    }

	@Override
	public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) 
	{
		return !player.isCreative();
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
		list.add(new TextComponentString(TextFormatting.AQUA + "Right click an entity to edit their equipment"));
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return true;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target,
			EnumHand hand) {
		if(target instanceof EntityLiving)
		{
			openArmorGUI(player, (EntityLiving) target);
			return true;
		}
		return false;
	}
	
	private static final void openArmorGUI(EntityPlayer player, EntityLiving living)
	{
		if(player.world.isRemote)
			return;
		EntityPlayerMP mp = (EntityPlayerMP) player;
		mp.closeContainer();
		mp.getNextWindowId();
		int windowId = mp.currentWindowId;
		PacketHandler.sendToClient(new PacketOpenGuiArmor(living, windowId), mp);
		Container c = new ContainerArmor(player.inventory, living);
		mp.openContainer = c;
		mp.openContainer.windowId = windowId;
		mp.openContainer.addListener(mp);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(mp, c));	   
	}
}
