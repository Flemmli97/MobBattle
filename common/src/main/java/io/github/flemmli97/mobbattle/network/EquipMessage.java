package io.github.flemmli97.mobbattle.network;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EquipMessage implements CustomPacketPayload {

    public static final Type<EquipMessage> TYPE = new Type<>(new ResourceLocation(MobBattle.MODID, "equip_msg"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipMessage> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, EquipMessage>() {
        @Override
        public EquipMessage decode(RegistryFriendlyByteBuf buf) {
            return new EquipMessage(buf.readBoolean() ? ItemStack.STREAM_CODEC.decode(buf) : ItemStack.EMPTY, buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, EquipMessage pkt) {
            buf.writeBoolean(!pkt.equipment.isEmpty());
            if (!pkt.equipment.isEmpty())
                ItemStack.STREAM_CODEC.encode(buf, pkt.equipment);
            buf.writeInt(pkt.entityId);
            buf.writeInt(pkt.slot);
        }
    };

    public final ItemStack equipment;
    public final int entityId;
    public final int slot;

    public EquipMessage(ItemStack stack, int entityId, int slot) {
        this.equipment = stack;
        this.entityId = entityId;
        this.slot = slot;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
