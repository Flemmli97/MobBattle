package com.flemmli97.mobbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = MobBattle.MODID, name = MobBattle.MODNAME, version = MobBattle.VERSION)
public class MobBattle {

    public static final String MODID = "mobbattle";
    public static final String MODNAME = "Mob Battle Mod";
    public static final String VERSION = "2.0.0[1.7.10]-final";
    public static final Logger logger = LogManager.getLogger(MobBattle.MODID);
        
    @Instance
    public static MobBattle instance = new MobBattle();
        
     
    @SidedProxy(clientSide="com.flemmli97.mobbattle.ClientProxy", serverSide="com.flemmli97.mobbattle.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
    
    public static CreativeTabs customTab = new CreativeTabs("mobbattle") {

		@Override
		public Item getTabIconItem() {
			ItemStack iStack = new ItemStack(ModItems.mobStick);
			return iStack.getItem();
		}

    };
}
    