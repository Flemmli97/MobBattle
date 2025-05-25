package io.github.flemmli97.mobbattle.neoforge;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.neoforge.client.ClientEvents;
import io.github.flemmli97.mobbattle.neoforge.handler.EventHandler;
import io.github.flemmli97.mobbattle.neoforge.registry.ModComponents;
import io.github.flemmli97.mobbattle.neoforge.registry.ModItems;
import io.github.flemmli97.mobbattle.neoforge.registry.ModMenuType;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
import io.github.flemmli97.mobbattle.network.C2SSpawnEgg;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(value = MobBattle.MODID)
public class MobBattleNeoForge {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobBattle.MODID);

    public MobBattleNeoForge(IEventBus modBus) {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC, MobBattle.MODID + "-client.toml");
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC, MobBattle.MODID + ".toml");
        NeoForge.EVENT_BUS.register(new EventHandler());
        modBus.addListener(MobBattleNeoForge::preInit);
        modBus.addListener(MobBattleNeoForge::creativeTabContents);
        modBus.addListener(MobBattleNeoForge::registerPackets);
        ModItems.ITEMS.register(modBus);
        ModMenuType.MENU_TYPE.register(modBus);
        ModComponents.COMPONENTS.register(modBus);
        if (FMLEnvironment.dist == Dist.CLIENT)
            ClientEvents.register(modBus);
        MobBattle.tenshiLib = ModList.get().isLoaded("tenshilib");
        MobBattle.customTab = TAB_REGISTER.register("tab", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.MOB_STICK.get()))
                .title(Component.translatable("mobbattle.tab")).build());
        TAB_REGISTER.register(modBus);
    }

    public static void preInit(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.EXTENDED_EGG.get(), (source, stack) -> {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            double x = source.center().x() + direction.getStepX();
            double y = source.pos().getY() + direction.getStepY() + 0.2;
            double z = source.center().z() + direction.getStepZ();
            BlockPos blockpos = BlockPos.containing(x, y, z);
            boolean spawned = ItemExtendedSpawnEgg.spawnEntity(source.level(), stack, blockpos.getX() + 0.5D, blockpos.getY(),
                    blockpos.getZ() + 0.5D, direction);
            if (spawned) {
                stack.shrink(1);
            }
            return stack;
        }));
    }

    public static void creativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MobBattle.customTab.get()) {
            ModItems.ITEMS.getEntries().forEach(holder -> event.accept(holder.get()));
        }
    }

    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MobBattle.MODID);
        registrar.playToServer(C2SEffectStack.TYPE, C2SEffectStack.STREAM_CODEC, (pkt, ctx) -> ctx.enqueueWork(() -> C2SEffectStack.handle(pkt, ctx.player(), ModItems.MOB_EFFECT_GIVE.get())));
        registrar.playToServer(C2SSpawnEgg.TYPE, C2SSpawnEgg.STREAM_CODEC, (pkt, ctx) -> ctx.enqueueWork(() -> C2SSpawnEgg.handle(pkt, ctx.player())));
        registrar.playToClient(S2CSpawnEggScreen.TYPE, S2CSpawnEggScreen.STREAM_CODEC, (pkt, ctx) -> ctx.enqueueWork(() -> S2CSpawnEggScreen.handle(pkt)));
    }
}
