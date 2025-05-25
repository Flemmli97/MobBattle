package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.items.MobEffectGive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class C2SEffectStack implements Packet {

    public static final ResourceLocation ID = new ResourceLocation(MobBattle.MODID, "c2s_effect_stack_update");

    private final String potion;
    private final int duration, amplifier;
    private final boolean particle;

    public C2SEffectStack(String potion, int duration, int amplifier, boolean particle) {
        this.potion = potion;
        this.duration = duration;
        this.amplifier = amplifier;
        this.particle = particle;
    }


    public static C2SEffectStack read(FriendlyByteBuf buf) {
        return new C2SEffectStack(buf.readUtf(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(C2SEffectStack pkt, ServerPlayer sender) {
        if (sender != null) {
            ItemStack stack = sender.getMainHandItem();
            if (!stack.isEmpty() && stack.getItem() instanceof MobEffectGive give) {
                give.updateFrom(stack, pkt.potion, pkt.duration, pkt.amplifier, pkt.particle);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.potion);
        buf.writeInt(this.duration);
        buf.writeInt(this.amplifier);
        buf.writeBoolean(this.particle);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
