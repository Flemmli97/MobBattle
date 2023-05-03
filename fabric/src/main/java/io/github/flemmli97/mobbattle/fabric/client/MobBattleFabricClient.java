package io.github.flemmli97.mobbattle.fabric.client;

import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.client.gui.MultiItemColor;
import io.github.flemmli97.mobbattle.fabric.ModItems;
import io.github.flemmli97.mobbattle.fabric.ModMenuType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screens.MenuScreens;

public class MobBattleFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(new MultiItemColor(), ModItems.spawner);
        WorldRenderEvents.END.register(ClientEvents::render);
        MenuScreens.register(ModMenuType.armorMenu, GuiArmor::new);
    }
}
