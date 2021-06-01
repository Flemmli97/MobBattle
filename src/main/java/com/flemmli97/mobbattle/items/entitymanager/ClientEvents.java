package com.flemmli97.mobbattle.items.entitymanager;

import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.MobArmy;
import com.flemmli97.mobbattle.items.MobEquip;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem() == ModItems.mobArmy) {
            MobArmy item = (MobArmy) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if (pos != null)
                renderBlockOutline(event.getMatrixStack(), player, pos, pos2, event.getPartialTicks());
        } else if (heldItem.getItem() == ModItems.mobEquip) {
            MobEquip item = (MobEquip) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if (pos != null)
                renderBlockOutline(event.getMatrixStack(), player, pos, pos2, event.getPartialTicks());
        }
    }

    private static void renderBlockOutline(MatrixStack stack, ClientPlayerEntity player, BlockPos pos, BlockPos pos2, float partialTicks) {
        AxisAlignedBB aabb = Utils.getBoundingBoxPositions(pos, pos2).shrink(0.1);
        ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        double d0 = -activerenderinfo.getProjectedView().x;
        double d1 = -activerenderinfo.getProjectedView().y;
        double d2 = -activerenderinfo.getProjectedView().z;
        if (aabb != null) {
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.lineWidth(2);
            RenderSystem.depthMask(false);
            WorldRenderer.drawBoundingBox(stack, Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().getBuffer(RenderType.LINES), aabb.grow(0.0020000000949949026D).offset(d0, d1, d2), 1, 0.5F, 0.5F, 1);
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }
}
