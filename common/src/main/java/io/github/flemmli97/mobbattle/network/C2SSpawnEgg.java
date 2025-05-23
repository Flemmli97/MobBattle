package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.ItemExtendedSpawnEgg;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class C2SSpawnEgg implements Packet {

    public static final ResourceLocation ID = new ResourceLocation(MobBattle.MODID, "c2s_spawn_egg");

    private final InteractionHand hand;
    private final String team;
    private final int amount, spacing;

    public C2SSpawnEgg(InteractionHand hand, String team, int amount, int spacing) {
        this.hand = hand;
        this.team = team;
        this.amount = amount;
        this.spacing = spacing;
    }

    public static C2SSpawnEgg read(FriendlyByteBuf buf) {
        return new C2SSpawnEgg(buf.readEnum(InteractionHand.class), buf.readUtf(), buf.readInt(), buf.readInt());
    }

    public static void handle(C2SSpawnEgg pkt, ServerPlayer sender) {
        if (sender != null) {
            ItemStack stack = sender.getItemInHand(pkt.hand);
            if (stack.getItem() instanceof ItemExtendedSpawnEgg) {
                ItemExtendedSpawnEgg.updateOptions(stack, new ItemExtendedSpawnEgg.SpawnOptions(pkt.team, pkt.amount, pkt.spacing));
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
        buf.writeUtf(this.team);
        buf.writeInt(this.amount);
        buf.writeInt(this.spacing);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
