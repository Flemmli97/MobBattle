package io.github.flemmli97.mobbattle.fabric.client;

import io.github.flemmli97.mobbattle.client.ClientHandler;
import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.fabric.registry.ModItems;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public class ClientEvents {

    public static void render(WorldRenderContext event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() == ModItems.mobArmy || heldItem.getItem() == ModItems.mobEquip) {
            AreaPositionComponent comp = heldItem.get(CrossPlatformStuff.INSTANCE.getComponentAreaSelection());
            if (comp != null && comp.first() != null && comp.second() != null)
                ClientHandler.renderBlockOutline(event.matrixStack(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), comp.first(), comp.second());
        }
    }
}
