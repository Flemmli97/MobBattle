package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.MobBattleTab;
import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import io.github.flemmli97.mobbattle.fabric.network.ServerPacketHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MobBattleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MobBattleTab.customTab = FabricItemGroupBuilder.build(
                new ResourceLocation("mobbattle", "tab"),
                () -> new ItemStack(ModItems.mobStick));
        ModItems.registerItems();
        ModMenuType.register();
        AttackEntityCallback.EVENT.register(EventHandler::attackCallback);
        ServerPacketHandler.register();
        Config.initConfig();
        MobBattle.tenshiLib = FabricLoader.getInstance().isModLoaded("tenshilib");
    }
}
