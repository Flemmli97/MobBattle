package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class C2SEffectStack implements CustomPacketPayload {

    public static final Type<C2SEffectStack> TYPE = new Type<>(MobBattle.of("c2s_effect_stack_update"));

    public static final StreamCodec<FriendlyByteBuf, C2SEffectStack> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public C2SEffectStack decode(FriendlyByteBuf buf) {
            return new C2SEffectStack(EffectComponent.STREAM_CODEC.decode(buf));
        }

        @Override
        public void encode(FriendlyByteBuf buf, C2SEffectStack pkt) {
            EffectComponent.STREAM_CODEC.encode(buf, pkt.data);
        }
    };

    public final EffectComponent data;

    public C2SEffectStack(EffectComponent compound) {
        this.data = compound;
    }

    public static void handle(C2SEffectStack msg, Player player, Item required) {
        if (player.getMainHandItem().getItem() != required)
            return;
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty())
            stack.set(CrossPlatformStuff.INSTANCE.getComponentEffect(), msg.data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
