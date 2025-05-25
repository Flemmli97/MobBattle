package io.github.flemmli97.mobbattle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.client.gui.GuiEffect;
import io.github.flemmli97.mobbattle.client.gui.SpawnEggScreen;
import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class ClientHandler {

    public static void openEffectGui() {
        Minecraft.getInstance().setScreen(new GuiEffect());
    }

    public static void renderBlockOutline(PoseStack stack, MultiBufferSource.BufferSource buffer, BlockPos pos, BlockPos pos2) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        stack.pushPose();
        stack.mulPose(camera.rotation().conjugate(new Quaternionf()));
        AABB aabb = Utils.getBoundingBoxPositions(pos, pos2).deflate(0.05);
        Vec3 vec = camera.getPosition();
        stack.translate(-vec.x, -vec.y, -vec.z);
        LevelRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), aabb, 1, 0.5F, 0.5F, 1);
        buffer.endBatch(RenderType.LINES);
        stack.popPose();
    }

    public static void openSpawneggGui(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new SpawnEggScreen(hand));
    }
}
