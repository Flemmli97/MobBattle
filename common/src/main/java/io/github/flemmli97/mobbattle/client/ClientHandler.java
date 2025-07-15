package io.github.flemmli97.mobbattle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.flemmli97.mobbattle.client.gui.GuiEffect;
import io.github.flemmli97.mobbattle.client.gui.SpawnEggScreen;
import io.github.flemmli97.mobbattle.common.components.UuidComponent;
import io.github.flemmli97.mobbattle.common.components.UuidListComponent;
import io.github.flemmli97.mobbattle.common.items.MobHighlightItem;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
        ShapeRenderer.renderLineBox(stack, buffer.getBuffer(RenderType.lines()), aabb, 1, 0.5F, 0.5F, 1);
        buffer.endBatch(RenderType.LINES);
        stack.popPose();
    }

    public static void openSpawneggGui(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new SpawnEggScreen(hand));
    }

    public static boolean handleEntityHighlight(Entity entity) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return false;
        if (isHighlighted(entity, player.getMainHandItem()))
            return true;
        return isHighlighted(entity, player.getOffhandItem());
    }

    private static boolean isHighlighted(Entity entity, ItemStack stack) {
        boolean highlight = false;
        if (stack.getItem() instanceof MobHighlightItem) {
            HitResult res = Minecraft.getInstance().hitResult;
            if (res != null && res.getType() == HitResult.Type.ENTITY) {
                Entity hit = ((EntityHitResult) res).getEntity();
                if (hit == entity)
                    return true;
            }
            UuidComponent id = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobUuid());
            if (id != null && id.uuid().isPresent()) {
                if (entity.getUUID().equals(id.uuid().get()))
                    return true;
            }
            UuidListComponent list = stack.get(CrossPlatformStuff.INSTANCE.getComponentMobGroupUuid());
            if (list != null && list.uuids().stream().anyMatch(uuid -> entity.getUUID().equals(uuid))) {
                return true;
            }
        }
        return highlight;
    }
}
