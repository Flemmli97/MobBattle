package io.github.flemmli97.mobbattle.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.fabric.ModItems;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.items.MobArmy;
import io.github.flemmli97.mobbattle.items.MobEquip;
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
        if (heldItem.getItem() == ModItems.mobArmy) {
            MobArmy item = (MobArmy) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if (pos != null)
                renderBlockOutline(event.matrixStack(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), pos, pos2);
        } else if (heldItem.getItem() == ModItems.mobEquip) {
            MobEquip item = (MobEquip) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if (pos != null)
                renderBlockOutline(event.matrixStack(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), pos, pos2);
        }
    }

    private static void renderBlockOutline(PoseStack stack, MultiBufferSource.BufferSource buffer, BlockPos pos, BlockPos pos2) {
        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        AABB aabb = Utils.getBoundingBoxPositions(pos, pos2).deflate(0.1);
        LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), aabb.inflate(0.002).move(-vec.x, -vec.y, -vec.z), 1, 0.5F, 0.5F, 1);
        buffer.endBatch(RenderType.LINES);
    }
}
