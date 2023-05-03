package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import io.github.flemmli97.mobbattle.fabric.network.ServerPacketHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MobBattleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModMenuType.register();
        AttackEntityCallback.EVENT.register(EventHandler::attackCallback);
        ServerPacketHandler.register();
        Config.initConfig();
        MobBattle.tenshiLib = FabricLoader.getInstance().isModLoaded("tenshilib");
        MobBattle.customTab = FabricItemGroup.builder(new ResourceLocation("mobbattle", "tab"))
                .title(Component.translatable("mobbattle.tab"))
                .icon(() -> new ItemStack(ModItems.mobStick))
                .build();
        ItemGroupEvents.modifyEntriesEvent(MobBattle.customTab).register(content -> {
            ModItems.modItems().forEach(content::accept);
        });
    }
}
