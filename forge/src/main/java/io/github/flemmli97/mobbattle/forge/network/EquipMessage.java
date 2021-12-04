package io.github.flemmli97.mobbattle.forge.network;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.forge.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EquipMessage {

    public ItemStack equipment;
    public int entityId;
    public int slot;

    public EquipMessage(ItemStack stack, int entityId, int slot) {
        this.equipment = stack;
        this.entityId = entityId;
        this.slot = slot;
    }

    public static EquipMessage fromBytes(FriendlyByteBuf buf) {
        CompoundTag compound = buf.readNbt();
        return new EquipMessage(compound.contains("Stack") ? ItemStack.of(compound.getCompound("Stack")) : ItemStack.EMPTY,
                compound.getInt("EntityID"), compound.getInt("Slot"));
    }

    public static void toBytes(EquipMessage msg, FriendlyByteBuf buf) {
        CompoundTag compound = new CompoundTag();
        CompoundTag tag = new CompoundTag();
        compound.putInt("EntityID", msg.entityId);
        if (msg.equipment != null)
            compound.put("Stack", msg.equipment.save(tag));
        compound.putInt("Slot", msg.slot);
        buf.writeNbt(compound);
    }

    public static void onMessage(EquipMessage msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getSender() == null || ctx.get().getSender().getMainHandItem().getItem() != ModItems.mobArmor.get())
            return;
        ctx.get().enqueueWork(() -> {
            Level world = ctx.get().getSender().level;
            Entity e = world.getEntity(msg.entityId);
            if (e instanceof Mob) {
                e.setItemSlot(MobBattle.slot[msg.slot], msg.equipment);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
