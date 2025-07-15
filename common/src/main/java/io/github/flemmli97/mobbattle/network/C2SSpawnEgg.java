package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.common.items.ItemExtendedSpawnEgg;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class C2SSpawnEgg implements CustomPacketPayload {

    public static final Type<C2SSpawnEgg> TYPE = new Type<>(MobBattle.of("c2s_spawn_egg"));

    public static final StreamCodec<FriendlyByteBuf, C2SSpawnEgg> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public C2SSpawnEgg decode(FriendlyByteBuf buf) {
            return new C2SSpawnEgg(buf.readEnum(InteractionHand.class), buf.readUtf(), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, C2SSpawnEgg pkt) {
            buf.writeEnum(pkt.hand);
            buf.writeUtf(pkt.team);
            buf.writeInt(pkt.amount);
            buf.writeInt(pkt.spacing);
        }
    };

    private final InteractionHand hand;
    private final String team;
    private final int amount, spacing;

    public C2SSpawnEgg(InteractionHand hand, String team, int amount, int spacing) {
        this.hand = hand;
        this.team = team;
        this.amount = amount;
        this.spacing = spacing;
    }

    public static void handle(C2SSpawnEgg pkt, Player sender) {
        if (sender != null) {
            ItemStack stack = sender.getItemInHand(pkt.hand);
            if (stack.getItem() instanceof ItemExtendedSpawnEgg) {
                stack.set(CrossPlatformStuff.INSTANCE.getComponentSpawnEggOptions(), new SpawnEggOptions(pkt.team, pkt.amount, pkt.spacing));
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
