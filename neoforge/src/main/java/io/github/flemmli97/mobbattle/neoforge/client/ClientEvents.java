package io.github.flemmli97.mobbattle.neoforge.client;

import io.github.flemmli97.mobbattle.client.BossBarItemColor;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.common.registry.MobBattleMenuTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents {

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(new ClientEvents());
        modBus.addListener(ClientEvents::spawnEggColor);
        modBus.addListener(ClientEvents::menuRegister);
        modBus.addListener(ClientEvents::keyRegister);
    }

    @SubscribeEvent
    public void render(RenderLevelStageEvent.AfterOpaqueFeatures event) {
        ClientHandler.render(event.getPoseStack());
    }

    @SubscribeEvent(receiveCanceled = true)
    public void keyEvent(ClientTickEvent.Post event) {
        ClientHandler.keyEvent();
    }

    public static void spawnEggColor(RegisterColorHandlersEvent.ItemTintSources e) {
        e.register(BossBarItemColor.ID, BossBarItemColor.CODEC);
    }

    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(MobBattleMenuTypes.ARMOR_MENU.get(), GuiArmor::new);
    }

    public static void sendPacketServer(CustomPacketPayload packet) {
        Minecraft.getInstance().getConnection().send(packet);
    }

    public static void keyRegister(RegisterKeyMappingsEvent event) {
        ClientHandler.registerKeyBinding(id -> {
            KeyMapping.Category category = new KeyMapping.Category(id);
            event.registerCategory(category);
            return category;
        }, event::register);
    }
}
