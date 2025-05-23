package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class S2CSpawnEggScreen implements Packet {

    public static final ResourceLocation ID = new ResourceLocation(MobBattle.MODID, "s2c_spawn_egg_screen");

    private final InteractionHand hand;

    public S2CSpawnEggScreen(InteractionHand hand) {
        this.hand = hand;
    }

    public static S2CSpawnEggScreen read(FriendlyByteBuf buf) {
        return new S2CSpawnEggScreen(buf.readEnum(InteractionHand.class));
    }

    public static void handle(S2CSpawnEggScreen pkt) {
        ClientHandler.openSpawneggGui(pkt.hand);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}