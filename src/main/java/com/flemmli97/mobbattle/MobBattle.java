package com.flemmli97.mobbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flemmli97.mobbattle.client.gui.MultiItemColor;
import com.flemmli97.mobbattle.items.entitymanager.EventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(value = MobBattle.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobBattle {

	public MobBattle() {
		ModLoadingContext.get().getActiveContainer().registerExtensionPoint(ExtensionPoint.GUIFACTORY, ()->ClientProxy::openGui);
	}
	
	public static final MobBattle instance = new MobBattle();
	
    public static final String MODID = "mobbattle";
    public static final Logger logger = LogManager.getLogger(MobBattle.MODID);
	
	public static boolean fate;
	public static boolean runecraftory;
	public static boolean animania;
	public static boolean mca;
	
	public IProxy proxy = DistExecutor.runForDist(()->ClientProxy::new, ()->ServerProxy::new);
	
    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent e) {
    	fate = ModList.get().isLoaded("fatemod");
    	runecraftory = ModList.get().isLoaded("runecraftory");
    	animania = ModList.get().isLoaded("animania");
    	mca = ModList.get().isLoaded("mca");
    	MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void spawnEggColor(ColorHandlerEvent.Item e) {
        e.getItemColors().register(new MultiItemColor(), ModItems.spawner);
    }
    
    //Cause atm the above event doesnt get called (or i cant find it getting called)
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void init(FMLLoadCompleteEvent e) {
        Minecraft.getInstance().getItemColors().register(new MultiItemColor(), ModItems.spawner);
    }
    
    public static ItemGroup customTab = new ItemGroup("mobbattle") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.mobStick);
		}
    };
}
    