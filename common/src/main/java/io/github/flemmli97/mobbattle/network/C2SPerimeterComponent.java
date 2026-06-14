package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.components.PerimeterComponent;
import io.github.flemmli97.mobbattle.common.items.PerimeterTool;
import io.github.flemmli97.mobbattle.common.registry.MobBattleDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class C2SPerimeterComponent implements CustomPacketPayload {

    public static final Type<C2SPerimeterComponent> TYPE = new Type<>(MobBattle.of("c2s_perimeter_tool"));

    public static final StreamCodec<FriendlyByteBuf, C2SPerimeterComponent> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public C2SPerimeterComponent decode(FriendlyByteBuf buf) {
            return new C2SPerimeterComponent(buf.readEnum(InteractionHand.class), PerimeterComponent.STREAM_CODEC.decode(buf));
        }

        @Override
        public void encode(FriendlyByteBuf buf, C2SPerimeterComponent pkt) {
            buf.writeEnum(pkt.hand);
            PerimeterComponent.STREAM_CODEC.encode(buf, pkt.component);
        }
    };

    private final InteractionHand hand;
    private final PerimeterComponent component;

    public C2SPerimeterComponent(InteractionHand hand, PerimeterComponent component) {
        this.hand = hand;
        this.component = component;
    }

    public static void handle(C2SPerimeterComponent pkt, Player sender) {
        if (sender != null) {
            ItemStack stack = sender.getItemInHand(pkt.hand);
            if (stack.getItem() instanceof PerimeterTool) {
                stack.set(MobBattleDataComponents.PERIMETER.get(), pkt.component);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
