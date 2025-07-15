package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.EventCalls;
import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import io.github.flemmli97.mobbattle.fabric.registry.ModComponents;
import io.github.flemmli97.mobbattle.fabric.registry.ModItems;
import io.github.flemmli97.mobbattle.fabric.registry.ModMenuType;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
import io.github.flemmli97.mobbattle.network.C2SSpawnEgg;
import io.github.flemmli97.mobbattle.network.S2CSpawnEggScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MobBattleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModMenuType.register();
        ModComponents.register();
        AttackEntityCallback.EVENT.register(EventHandler::attackCallback);
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> EventCalls.handleJoinLevel(entity));
        registerPackets();
        ConfigLoader.initConfig();
        MobBattle.tenshiLib = FabricLoader.getInstance().isModLoaded("tenshilib");
        ResourceLocation tab = MobBattle.of("tab");
        CreativeModeTab creativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tab, FabricItemGroup.builder()
                .title(Component.translatable("mobbattle.tab"))
                .icon(() -> new ItemStack(ModItems.mobStick))
                .build());
        MobBattle.customTab = () -> creativeModeTab;
        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab)).register(content -> {
            ModItems.modItems().forEach(content::accept);
        });
    }

    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(C2SEffectStack.TYPE, C2SEffectStack.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SEffectStack.TYPE, (pkt, ctx) -> C2SEffectStack.handle(pkt, ctx.player(), ModItems.mobEffectGiver));
        PayloadTypeRegistry.playC2S().register(C2SSpawnEgg.TYPE, C2SSpawnEgg.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SSpawnEgg.TYPE, (pkt, ctx) -> C2SSpawnEgg.handle(pkt, ctx.player()));
        PayloadTypeRegistry.playS2C().register(S2CSpawnEggScreen.TYPE, S2CSpawnEggScreen.STREAM_CODEC);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(S2CSpawnEggScreen.TYPE, (pkt, ctx) -> S2CSpawnEggScreen.handle(pkt));
        }
    }
}
