package io.github.flemmli97.mobbattle.forge.network;

import io.github.flemmli97.mobbattle.forge.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemStackUpdate {

    public CompoundTag compound;

    public ItemStackUpdate(CompoundTag compound) {
        this.compound = compound;
    }

    public static ItemStackUpdate fromBytes(FriendlyByteBuf buf) {
        return new ItemStackUpdate(buf.readNbt());
    }

    public static void toBytes(ItemStackUpdate msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.compound);
    }

    public static void onMessage(ItemStackUpdate msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getSender() == null || ctx.get().getSender().getMainHandItem().getItem() != ModItems.mobEffectGiver.get())
            return;
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty())
                stack.setTag(msg.compound);
        });
        ctx.get().setPacketHandled(true);
    }
}
