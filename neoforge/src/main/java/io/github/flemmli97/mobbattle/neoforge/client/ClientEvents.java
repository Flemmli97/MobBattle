package io.github.flemmli97.mobbattle.neoforge.client;

import io.github.flemmli97.mobbattle.client.BossBarItemColor;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.client.ItemModelProps;
import io.github.flemmli97.mobbattle.client.MultiItemColor;
import io.github.flemmli97.mobbattle.client.gui.ArmorScreen;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.common.registry.MobBattleMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents {

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(new ClientEvents());
        modBus.addListener(ClientEvents::clientSetup);
        modBus.addListener(ClientEvents::spawnEggColor);
        modBus.addListener(ClientEvents::menuRegister);
        modBus.addListener(ClientEvents::keyRegister);
    }

    @SubscribeEvent
    public void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)
            return;
        ClientHandler.render(event.getPoseStack());
    }

    @SubscribeEvent(receiveCanceled = true)
    public void keyEvent(ClientTickEvent.Post event) {
        ClientHandler.keyEvent();
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(MobBattleItems.PERIMETER_TOOL.get(), ItemModelProps.PERIMETER_REMOVE, ItemModelProps.PERIMETER_REMOVE_PROPERTY));
    }

    public static void spawnEggColor(RegisterColorHandlersEvent.Item e) {
        e.register(new MultiItemColor(), MobBattleItems.EXTENDED_EGG.get());
        e.register(new BossBarItemColor(), MobBattleItems.BOSS_BAR_ADDER.get());
    }

    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(MobBattleMenuTypes.ARMOR_MENU.get(), ArmorScreen::new);
    }

    public static void sendPacketServer(CustomPacketPayload packet) {
        Minecraft.getInstance().getConnection().send(packet);
    }

    public static void keyRegister(RegisterKeyMappingsEvent event) {
        ClientHandler.registerKeyBinding(event::register);
    }
}
