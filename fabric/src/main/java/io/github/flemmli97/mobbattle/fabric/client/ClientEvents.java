package io.github.flemmli97.mobbattle.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.fabric.registry.ModItems;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ClientEvents {

    public static void render(WorldRenderContext event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() == ModItems.mobArmy || heldItem.getItem() == ModItems.mobEquip) {
            AreaPositionComponent comp = heldItem.get(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
            if (comp != null && comp.first() != null && comp.second() != null)
                renderBlockOutline(event.matrixStack(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), comp.first(), comp.second());
        }
    }

    private static void renderBlockOutline(PoseStack stack, MultiBufferSource.BufferSource buffer, BlockPos pos, BlockPos pos2) {
        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        AABB aabb = Utils.getBoundingBoxPositions(pos, pos2).deflate(0.1);
        LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), aabb.inflate(0.002).move(-vec.x, -vec.y, -vec.z), 1, 0.5F, 0.5F, 1);
        buffer.endBatch(RenderType.LINES);
    }
}
