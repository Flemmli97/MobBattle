package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.handler.ClientEvents;
import com.flemmli97.mobbattle.handler.EventHandler;
import com.flemmli97.mobbattle.inv.ContainerArmor;
import com.flemmli97.mobbattle.network.PacketHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

@Mod(value = MobBattle.MODID)
public class MobBattle {

    public MobBattle() {
        //This is stupid... 2 files for 2 config values
        ModLoadingContext.get().registerConfig(Type.CLIENT, Config.clientSpec, MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.commonSpec, MODID + ".toml");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::register);
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(MobBattle::preInit);
    }

    public static final String MODID = "mobbattle";
    public static final Logger logger = LogManager.getLogger(MobBattle.MODID);

    public static boolean tenshiLib;
    public static boolean animania;
    public static boolean mca;

    public static final EquipmentSlotType[] slot = {EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND, EquipmentSlotType.HEAD,
            EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    public static ContainerType<ContainerArmor> type = new ContainerType<>(
            (windowID, playerInv) -> new ContainerArmor(windowID, playerInv, null));

    public static void preInit(FMLCommonSetupEvent e) {
        tenshiLib = ModList.get().getModContainerById("tenshilib")
                .map(container -> new DefaultArtifactVersion("1.16.5-1.4.0").compareTo(container.getModInfo().getVersion()) <= 0).orElse(false);
        animania = ModList.get().isLoaded("animania");
        mca = ModList.get().isLoaded("mca");
        PacketHandler.register();
    }
}
