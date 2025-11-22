package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.items.ExtendedItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class C2SItemFunctionPress implements CustomPacketPayload {

    public static final Type<C2SItemFunctionPress> TYPE = new Type<>(MobBattle.of("c2s_item_function_press"));

    public static final C2SItemFunctionPress INSTANCE = new C2SItemFunctionPress();
    public static final StreamCodec<FriendlyByteBuf, C2SItemFunctionPress> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private C2SItemFunctionPress() {
    }

    public static void handle(C2SItemFunctionPress msg, Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!triggerPress(stack, player)) {
            stack = player.getOffhandItem();
            triggerPress(stack, player);
        }
    }

    private static boolean triggerPress(ItemStack stack, Player player) {
        if (!stack.isEmpty() && stack.getItem() instanceof ExtendedItem ext) {
            return ext.onFunctionPress(stack, player);
        }
        return false;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
