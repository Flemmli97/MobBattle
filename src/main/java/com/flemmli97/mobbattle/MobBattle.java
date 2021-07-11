package com.flemmli97.mobbattle;

import com.flemmli97.mobbattle.client.gui.MultiItemColor;
import com.flemmli97.mobbattle.inv.ContainerArmor;
import com.flemmli97.mobbattle.items.entitymanager.ClientEvents;
import com.flemmli97.mobbattle.items.entitymanager.EventHandler;
import com.flemmli97.mobbattle.network.PacketHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

@Mod(value = MobBattle.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobBattle {

    public MobBattle() {
        //This is stupid... 2 files for 2 config values
        ModLoadingContext.get().registerConfig(Type.CLIENT, Config.clientSpec, MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.commonSpec, MODID + ".toml");
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

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent e) {
        tenshiLib = ModList.get().getModContainerById("tenshilib")
                .map(container -> new DefaultArtifactVersion("1.16.4-1.4.0").compareTo(container.getModInfo().getVersion()) >= 0).orElse(false);
        animania = ModList.get().isLoaded("animania");
        mca = ModList.get().isLoaded("mca");
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::register);
    }

    @SubscribeEvent
    public static void registerTextureSprite(TextureStitchEvent.Pre event) {
        ResourceLocation res = new ResourceLocation(MobBattle.MODID, "gui/armor_slot_sword");
        event.addSprite(res);
    }

    @SubscribeEvent
    public static void spawnEggColor(ColorHandlerEvent.Item e) {
        e.getItemColors().register(new MultiItemColor(), ModItems.spawner);
    }
}
