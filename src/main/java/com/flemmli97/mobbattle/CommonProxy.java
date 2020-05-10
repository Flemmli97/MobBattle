package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.client.gui.GuiHandler;
import com.flemmli97.mobbattle.items.entitymanager.EventHandler;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public static final EntityEquipmentSlot[] slot = {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MobBattle.MODID);
    public static boolean fate;
    public static boolean runecraftory;
    public static boolean animania;
    public static boolean mca;

    public void preInit(FMLPreInitializationEvent e) {
        Config.load();
        dispatcher.registerMessage(EquipMessage.Handler.class, EquipMessage.class, 0, Side.SERVER);
        dispatcher.registerMessage(ItemStackUpdate.Handler.class, ItemStackUpdate.class, 1, Side.SERVER);
    }

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(MobBattle.instance, new GuiHandler());
        if(Loader.isModLoaded("runecraftory")){
            runecraftory = true;
        }
        if(Loader.isModLoaded("fatemod")){
            fate = true;
        }
        if(Loader.isModLoaded("animania")){
            animania = true;
        }
        if(Loader.isModLoaded("mca")){
            mca = true;
        }
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    public static final void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }
}