package io.github.flemmli97.mobbattle.fabric.network;

import io.github.flemmli97.mobbattle.fabric.registry.ModItems;
import io.github.flemmli97.mobbattle.network.C2SEffectStack;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PacketHandler {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(C2SEffectStack.TYPE, C2SEffectStack.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SEffectStack.TYPE, (pkt, ctx) -> {
            C2SEffectStack.handle(pkt, ctx.player(), ModItems.mobEffectGiver);
        });
    }
}
