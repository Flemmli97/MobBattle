package io.github.flemmli97.mobbattle.fabric.client;

import io.github.flemmli97.mobbattle.client.BossBarItemColor;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.client.MultiItemColor;
import io.github.flemmli97.mobbattle.client.gui.ArmorScreen;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.common.registry.MobBattleMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screens.MenuScreens;

public class MobBattleFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(new MultiItemColor(), MobBattleItems.EXTENDED_EGG.get());
        ColorProviderRegistry.ITEM.register(new BossBarItemColor(), MobBattleItems.BOSS_BAR_ADDER.get());
        WorldRenderEvents.END.register((event) -> ClientHandler.render(event.matrixStack()));
        MenuScreens.register(MobBattleMenuTypes.ARMOR_MENU.get(), ArmorScreen::new);
        ClientHandler.registerKeyBinding(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientHandler.keyEvent());
    }
}
