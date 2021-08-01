package com.flemmli97.mobbattle.handler;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.client.gui.MultiItemColor;
import com.flemmli97.mobbattle.items.MobArmy;
import com.flemmli97.mobbattle.items.MobEquip;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ClientEvents::registerTextureSprite);
        modbus.addListener(ClientEvents::spawnEggColor);
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
                renderBlockOutline(event.getMatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getCrumblingBufferSource(), pos, pos2);
        } else if (heldItem.getItem() == ModItems.mobEquip) {
            MobEquip item = (MobEquip) heldItem.getItem();
            BlockPos pos = item.getSelPos(heldItem)[0];
            BlockPos pos2 = item.getSelPos(heldItem)[1];
            if (pos != null)
                renderBlockOutline(event.getMatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getCrumblingBufferSource(), pos, pos2);
        }
    }

    private static void renderBlockOutline(MatrixStack stack, IRenderTypeBuffer.Impl buffer, BlockPos pos, BlockPos pos2) {
        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        AxisAlignedBB aabb = Utils.getBoundingBoxPositions(pos, pos2).shrink(0.1);
        WorldRenderer.drawBoundingBox(stack, buffer.getBuffer(RenderType.getLines()), aabb.grow(0.002).offset(-vec.x, -vec.y, -vec.z), 1, 0.5F, 0.5F, 1);
        buffer.finish(RenderType.LINES);
    }

    public static void registerTextureSprite(TextureStitchEvent.Pre event) {
        ResourceLocation res = new ResourceLocation(MobBattle.MODID, "gui/armor_slot_sword");
        event.addSprite(res);
    }

    public static void spawnEggColor(ColorHandlerEvent.Item e) {
        e.getItemColors().register(new MultiItemColor(), ModItems.spawner);
    }
}
