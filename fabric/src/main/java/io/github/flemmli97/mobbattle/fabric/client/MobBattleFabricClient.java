package io.github.flemmli97.mobbattle.fabric.client;

import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.fabric.registry.ModMenuType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screens.MenuScreens;

public class MobBattleFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END.register(ClientEvents::render);
        MenuScreens.register(ModMenuType.armorMenu, GuiArmor::new);
    }
}
