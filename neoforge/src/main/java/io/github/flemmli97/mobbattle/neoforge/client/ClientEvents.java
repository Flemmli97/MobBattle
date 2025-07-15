package io.github.flemmli97.mobbattle.neoforge.client;

import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.client.MultiItemColor;
import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.neoforge.registry.ModItems;
import io.github.flemmli97.mobbattle.neoforge.registry.ModMenuType;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents {

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(new ClientEvents());
        modBus.addListener(ClientEvents::spawnEggColor);
        modBus.addListener(ClientEvents::menuRegister);
    }

    @SubscribeEvent
    public void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)
            return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() == ModItems.MOB_ARMY.get() || heldItem.getItem() == ModItems.MOB_EQUIP.get()) {
            AreaPositionComponent comp = heldItem.get(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
            if (comp != null && comp.first() != null && comp.second() != null)
                ClientHandler.renderBlockOutline(event.getPoseStack(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), comp.first(), comp.second());
        }
    }

    public static void spawnEggColor(RegisterColorHandlersEvent.Item e) {
        e.register(new MultiItemColor(), ModItems.EXTENDED_EGG.get());
    }

    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(ModMenuType.ARMOR_MENU.get(), GuiArmor::new);
    }

    public static void sendPacketServer(CustomPacketPayload packet) {
        Minecraft.getInstance().getConnection().send(packet);
    }
}
