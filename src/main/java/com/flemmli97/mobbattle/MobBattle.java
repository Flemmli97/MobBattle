package com.flemmli97.mobbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MobBattle.MODID, name = MobBattle.MODNAME, version = MobBattle.VERSION)
public class MobBattle {

    public static final String MODID = "mobbattle";
    public static final String MODNAME = "Mob Battle Mod";
    public static final String VERSION = "2.2.2[1.11.2]";
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
		public ItemStack getTabIconItem() {
			ItemStack iStack = new ItemStack(ModItems.mobStick);
			return iStack;
			
		}

    };
}
    