package io.github.flemmli97.mobbattle.fabric.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.fabric.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ServerPacketHandler {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PacketID.equipMessage, ServerPacketHandler::equipMessage);
        ServerPlayNetworking.registerGlobalReceiver(PacketID.effectMessage, ServerPacketHandler::effectMessage);
    }

    private static void equipMessage(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (player == null || player.getMainHandItem().getItem() != ModItems.mobArmor)
            return;
        CompoundTag compound = buf.readNbt();
        ItemStack equipment = compound.contains("Stack") ? ItemStack.of(compound.getCompound("Stack")) : ItemStack.EMPTY;
        int entityId = compound.getInt("EntityID");
        int slot = compound.getInt("Slot");
        Level world = player.level();
        Entity e = world.getEntity(entityId);
        if (e instanceof Mob) {
            e.setItemSlot(MobBattle.slot[slot], equipment);
        }
    }

    private static void effectMessage(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (player == null || player.getMainHandItem().getItem() != ModItems.mobEffectGiver)
            return;
        CompoundTag compound = buf.readNbt();
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty())
            stack.setTag(compound);
    }
}
