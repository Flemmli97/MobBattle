package io.github.flemmli97.mobbattle.fabric.client;

import io.github.flemmli97.mobbattle.client.BossBarItemColor;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.client.gui.ArmorScreen;
import io.github.flemmli97.mobbattle.common.registry.MobBattleMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;

public class MobBattleFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTintSources.ID_MAPPER.put(BossBarItemColor.ID, BossBarItemColor.CODEC);
        LevelRenderEvents.AFTER_SOLID_FEATURES.register((event) -> ClientHandler.render(event.poseStack()));
        MenuScreens.register(MobBattleMenuTypes.ARMOR_MENU.get(), ArmorScreen::new);
        ClientHandler.registerKeyBinding(KeyMapping.Category::register, KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientHandler.keyEvent());
    }
}
