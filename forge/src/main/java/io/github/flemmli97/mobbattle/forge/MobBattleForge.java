package io.github.flemmli97.mobbattle.forge;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.MobBattleTab;
import io.github.flemmli97.mobbattle.forge.client.ClientEvents;
import io.github.flemmli97.mobbattle.forge.handler.EventHandler;
import io.github.flemmli97.mobbattle.forge.network.PacketHandler;
import io.github.flemmli97.mobbattle.forge.registry.ModItems;
import io.github.flemmli97.mobbattle.forge.registry.ModMenuType;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(value = MobBattle.MODID)
public class MobBattleForge {

    public MobBattleForge() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, Config.clientSpec, MobBattle.MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.commonSpec, MobBattle.MODID + ".toml");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(MobBattleForge::preInit);
        ModItems.ITEMS.register(modbus);
        ModMenuType.MENU_TYPE.register(modbus);
        if (FMLEnvironment.dist == Dist.CLIENT)
            ClientEvents.register();
        MobBattleTab.customTab = new CreativeModeTab("mobbattle.tab") {

            @Override
            public ItemStack makeIcon() {
                return new ItemStack(ModItems.MOB_STICK.get());
            }
        };
        MobBattle.tenshiLib = ModList.get().isLoaded("tenshilib");
    }

    public static void preInit(FMLCommonSetupEvent e) {
        PacketHandler.register();
        e.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.SPAWNER.get(), (source, stack) -> {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            double x = source.x() + direction.getStepX();
            double y = source.getPos().getY() + direction.getStepY() + 0.2;
            double z = source.z() + direction.getStepZ();
            BlockPos blockpos = new BlockPos(x, y, z);
            boolean spawned = ItemExtendedSpawnEgg.spawnEntity(source.getLevel(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D, direction);
            if (spawned) {
                stack.shrink(1);
            }
            return stack;
        }));
    }
}
