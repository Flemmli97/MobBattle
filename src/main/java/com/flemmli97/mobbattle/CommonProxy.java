package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.items.entitymanager.EventHandler;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
	
	public static final EntityEquipmentSlot slot[] = {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD
			,EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MobBattle.MODID);
	
    public void preInit(FMLPreInitializationEvent e) {
    	ModItems.init();
    	dispatcher.registerMessage(EquipMessage.Handler.class, EquipMessage.class, 0, Side.SERVER);
    }

    public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public void postInit(FMLPostInitializationEvent e) {
    }
    
    public static void openArmorGui(EntityPlayer player, EntityLivingBase target)
	{
		if(player instanceof EntityPlayerMP)
		{
			Container container = new ContainerArmor(player.inventory, (EntityLiving) target);
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
            entityPlayerMP.getNextWindowId();
            entityPlayerMP.closeContainer();
            int windowId = entityPlayerMP.currentWindowId;
            entityPlayerMP.openContainer = container;
            entityPlayerMP.openContainer.windowId = windowId;
            entityPlayerMP.openContainer.addListener(entityPlayerMP);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, container));
		}
		else if(FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
			FMLCommonHandler.instance().showGuiScreen(new GuiArmor(player.inventory, (EntityLiving) target));
	}
	
	public static final void sendToServer(IMessage message) {
		dispatcher.sendToServer(message);
	}
}