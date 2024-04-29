package io.github.flemmli97.mobbattle.fabric.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.fabric.registry.ModComponents;
import io.github.flemmli97.mobbattle.fabric.registry.ModItems;
import io.github.flemmli97.mobbattle.network.EffectGiveUpdate;
import io.github.flemmli97.mobbattle.network.EquipMessage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ServerPacketHandler {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(EffectGiveUpdate.TYPE, EffectGiveUpdate.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(EquipMessage.TYPE, EquipMessage.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(EquipMessage.TYPE, ServerPacketHandler::equipMessage);
        ServerPlayNetworking.registerGlobalReceiver(EffectGiveUpdate.TYPE, ServerPacketHandler::effectMessage);
    }

    private static void equipMessage(EquipMessage pkt, ServerPlayNetworking.Context ctx) {
        if (ctx.player().getMainHandItem().getItem() != ModItems.mobArmor)
            return;
        Level world = ctx.player().level();
        Entity e = world.getEntity(pkt.entityId);
        if (e instanceof Mob mob) {
            mob.setItemSlot(MobBattle.SLOT[pkt.slot], pkt.equipment);
        }
    }

    private static void effectMessage(EffectGiveUpdate pkt, ServerPlayNetworking.Context ctx) {
        if (ctx.player().getMainHandItem().getItem() != ModItems.mobEffectGiver)
            return;
        Player player = ctx.player();
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty())
            stack.set(ModComponents.EFFECT, pkt.data);
    }
}
