package io.github.flemmli97.mobbattle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.common.utils.PerimeterData;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class ClientPerimeterData {

    private static PerimeterData.Perimeter perimeter;

    public static PerimeterData.Perimeter getPerimeter() {
        return perimeter;
    }

    public static void setPerimeter(PerimeterData.Perimeter perimeter) {
        ClientPerimeterData.perimeter = perimeter;
    }

    public static void render(Player player, PoseStack stack) {
        if (perimeter == null)
            return;
        if (player.getMainHandItem().is(MobBattleItems.PERIMETER_TOOL.get()) || player.getOffhandItem().is(MobBattleItems.PERIMETER_TOOL.get())) {
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            AABB aabb = Utils.getBoundingBoxPositions(perimeter.center(), null);
            ClientHandler.renderShapeOutline(stack, buffer, Shapes.create(aabb), ARGB.colorFromFloat(0.5F, 0.5F, 1, 1));
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 vec = camera.position();
            stack.pushPose();
            stack.translate(-vec.x, -vec.y, -vec.z);
            switch (perimeter.shape()) {
                case CIRCLE -> {
                    double pY = Mth.lerp(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false),
                            Minecraft.getInstance().player.yOld, Minecraft.getInstance().player.getY());
                    float minY = Minecraft.getInstance().level.getMinY();
                    Vec3 center = Vec3.atCenterOf(perimeter.center());
                    if (perimeter.inner() != perimeter.outer()) {
                        renderCylinder(buffer, stack, center, perimeter.inner(), minY, (float) pY, 1, 1, 0, 0.3F);
                    }
                    renderCylinder(buffer, stack, center, perimeter.outer(), minY, (float) pY, 0.5F, 0, 0, 0.3F);
                }
                case SQUARE -> {
                    double pY = Mth.lerp(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false),
                            Minecraft.getInstance().player.yOld, Minecraft.getInstance().player.getY());
                    AABB perimeterAABB = new AABB(perimeter.center().getX(), Minecraft.getInstance().level.getMinY(), perimeter.center().getZ(),
                            perimeter.center().getX() + 1, pY + 1, perimeter.center().getZ() + 1);
                    if (perimeter.inner() != perimeter.outer()) {
                        renderWall(buffer, stack, perimeterAABB.inflate(perimeter.inner(), 0, perimeter.inner()), 1, 1, 0, 0.3F);
                    }
                    renderWall(buffer, stack, perimeterAABB.inflate(perimeter.outer(), 0, perimeter.outer()), 0.5F, 0, 0, 0.3F);
                }
            }
            stack.popPose();
        }
    }

    private static void renderWall(MultiBufferSource buffer, PoseStack stack, AABB aabb, float red, float green, float blue, float alpha) {
        VertexConsumer consumer = buffer.getBuffer(RenderTypes.debugFilledBox());
        PoseStack.Pose pose = stack.last();
        float minX = (float) aabb.minX;
        float minY = (float) aabb.minY;
        float minZ = (float) aabb.minZ;
        float maxX = (float) aabb.maxX;
        float maxY = (float) aabb.maxY;
        float maxZ = (float) aabb.maxZ;
        // West side
        consumer.addVertex(pose, minX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, maxY, minZ).setColor(red, green, blue, alpha);

        consumer.addVertex(pose, minX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, minY, minZ).setColor(red, green, blue, alpha);

        // East side
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, minY, minZ).setColor(red, green, blue, alpha);

        consumer.addVertex(pose, maxX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // North side
        consumer.addVertex(pose, minX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, minY, minZ).setColor(red, green, blue, alpha);

        consumer.addVertex(pose, minX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // South side
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, minY, maxZ).setColor(red, green, blue, alpha);

        consumer.addVertex(pose, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
    }


    private static void renderCylinder(MultiBufferSource buffer, PoseStack stack, Vec3 pos, double radius,
                                       float minY, float maxY,
                                       float red, float green, float blue, float alpha) {
        VertexConsumer consumer = buffer.getBuffer(RenderTypes.debugFilledBox());
        PoseStack.Pose pose = stack.last();
        int precision = 24;
        float step = Mth.PI / precision;
        for (int i = 0; i < precision * 2; ++i) {
            float theta = i * step;
            float thetaNext = theta + step;
            float x = (float) (pos.x() + radius * Math.cos(theta));
            float z = (float) (pos.z() + radius * Math.sin(theta));
            float xN = (float) (pos.x() + radius * Math.cos(thetaNext));
            float zN = (float) (pos.z() + radius * Math.sin(thetaNext));
            consumer.addVertex(pose, x, minY, z).setColor(red, green, blue, alpha);
            consumer.addVertex(pose, x, maxY, z).setColor(red, green, blue, alpha);
            consumer.addVertex(pose, xN, maxY, zN).setColor(red, green, blue, alpha);
            consumer.addVertex(pose, xN, minY, zN).setColor(red, green, blue, alpha);

            consumer.addVertex(pose, xN, minY, zN).setColor(red, green, blue, alpha);
            consumer.addVertex(pose, xN, maxY, zN).setColor(red, green, blue, alpha);
            consumer.addVertex(pose, x, maxY, z).setColor(red, green, blue, alpha);
            consumer.addVertex(pose, x, minY, z).setColor(red, green, blue, alpha);
        }
    }
}
