package io.github.flemmli97.mobbattle.forge;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.forge.client.ClientEvents;
import io.github.flemmli97.mobbattle.forge.handler.EventHandler;
import io.github.flemmli97.mobbattle.forge.network.PacketHandler;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;

@Mod(value = MobBattle.MODID)
public class MobBattleForge {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobBattle.MODID);

    public MobBattleForge() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, Config.clientSpec, MobBattle.MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.commonSpec, MobBattle.MODID + ".toml");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(MobBattleForge::preInit);
        modbus.addListener(MobBattleForge::creativeTabContents);
        ModItems.ITEMS.register(modbus);
        ModMenuType.MENU_TYPE.register(modbus);
        if (FMLEnvironment.dist == Dist.CLIENT)
            ClientEvents.register();
        MobBattle.tenshiLib = ModList.get().isLoaded("tenshilib");
        MobBattle.customTab = TAB_REGISTER.register("tab", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.mobStick.get()))
                .title(Component.translatable("mobbattle.tab")).build());
        TAB_REGISTER.register(modbus);
    }

    public static void preInit(FMLCommonSetupEvent e) {
        PacketHandler.register();
        e.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.spawner.get(), (source, stack) -> {
            Direction enumfacing = source.getBlockState().getValue(DispenserBlock.FACING);
            double x = source.x() + enumfacing.getStepX();
            double y = source.getPos().getY() + enumfacing.getStepY() + 0.2;
            double z = source.z() + enumfacing.getStepZ();
            BlockPos blockpos = BlockPos.containing(x, y, z);
            Entity entity = ItemExtendedSpawnEgg.spawnEntity(source.getLevel(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D);
            if (entity != null) {
                stack.shrink(1);
                if (stack.hasCustomHoverName() && entity instanceof Mob) {
                    Utils.updateEntity(stack.getHoverName().getString(), (Mob) entity);
                }
            }
            return stack;
        }));
    }

    public static void creativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MobBattle.customTab.get()) {
            ModItems.ITEMS.getEntries().forEach(event::accept);
        }
    }
}
