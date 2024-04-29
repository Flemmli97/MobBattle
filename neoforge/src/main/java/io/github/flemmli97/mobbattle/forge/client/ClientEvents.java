package io.github.flemmli97.mobbattle.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.client.gui.GuiArmor;
import io.github.flemmli97.mobbattle.client.gui.MultiItemColor;
import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.forge.registry.ModItems;
import io.github.flemmli97.mobbattle.forge.registry.ModMenuType;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
                renderBlockOutline(event.getPoseStack(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), comp.first(), comp.second());
        }
    }

    private static void renderBlockOutline(PoseStack stack, MultiBufferSource.BufferSource buffer, BlockPos pos, BlockPos pos2) {
        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        AABB aabb = Utils.getBoundingBoxPositions(pos, pos2).deflate(0.1);
        LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), aabb.inflate(0.002).move(-vec.x, -vec.y, -vec.z), 1, 0.5F, 0.5F, 1);
        buffer.endBatch(RenderType.LINES);
    }

    public static void spawnEggColor(RegisterColorHandlersEvent.Item e) {
        e.register(new MultiItemColor(), ModItems.EXTENDED_EGG.get());
    }

    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(ModMenuType.ARMOR_MENU.get(), GuiArmor::new);
    }
}
