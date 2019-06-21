package com.flemmli97.mobbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flemmli97.mobbattle.network.PacketHandler;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = MobBattle.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobBattle {

	public MobBattle() {
		//This is stupid... 2 files for 2 config values
		ModLoadingContext.get().registerConfig(Type.CLIENT, Config.clientSpec, MODID+"-client.toml");
		ModLoadingContext.get().registerConfig(Type.SERVER, Config.serverSpec, MODID+".toml");
	}
		
    public static final String MODID = "mobbattle";
    public static final Logger logger = LogManager.getLogger(MobBattle.MODID);
	
	public static boolean fate;
	public static boolean runecraftory;
	public static boolean animania;
	public static boolean mca;
	
	public static IProxy proxy = DistExecutor.runForDist( ()->ClientProxy::new, ()->ServerProxy::new);
	
	public static final EntityEquipmentSlot slot[] = {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD
			,EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	
    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent e) {
    	fate = ModList.get().isLoaded("fatemod");
    	runecraftory = ModList.get().isLoaded("runecraftory");
    	animania = ModList.get().isLoaded("animania");
    	mca = ModList.get().isLoaded("mca");
    	PacketHandler.register();
    }
    
    public static ItemGroup customTab = new ItemGroup("mobbattle") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.mobStick);
		}
    };
}
    