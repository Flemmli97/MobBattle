package io.github.flemmli97.mobbattle.forge.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.forge.registry.ModComponents;
import io.github.flemmli97.mobbattle.forge.registry.ModItems;
import io.github.flemmli97.mobbattle.network.EffectGiveUpdate;
import io.github.flemmli97.mobbattle.network.EquipMessage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PayloadHandlers {

    public static void equipMsgHandler(EquipMessage msg, IPayloadContext ctx) {
        if (ctx.player().getMainHandItem().getItem() != ModItems.MOB_ARMOR.get())
            return;
        ctx.enqueueWork(() -> {
            Level world = ctx.player().level();
            Entity e = world.getEntity(msg.entityId);
            if (e instanceof Mob mob) {
                mob.setItemSlot(MobBattle.SLOT[msg.slot], msg.equipment);
            }
        });
    }

    public static void effectMsgHandler(EffectGiveUpdate msg, IPayloadContext ctx) {
        if (ctx.player().getMainHandItem().getItem() != ModItems.MOB_EFFECT_GIVE.get())
            return;
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty())
                stack.set(ModComponents.EFFECT.get(), msg.data);
        });
    }
}
