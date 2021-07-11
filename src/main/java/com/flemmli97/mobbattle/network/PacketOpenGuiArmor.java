package com.flemmli97.mobbattle.network;

import com.flemmli97.mobbattle.client.ClientOpenGuiHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGuiArmor {

    private final int entityID;
    private final int windowID;

    public PacketOpenGuiArmor(MobEntity entity, int windowID) {
        this.entityID = entity.getEntityId();
        this.windowID = windowID;
    }

    private PacketOpenGuiArmor(int entityID, int windowID) {
        this.entityID = entityID;
        this.windowID = windowID;
    }

    public static PacketOpenGuiArmor fromBytes(PacketBuffer buf) {
        return new PacketOpenGuiArmor(buf.readInt(), buf.readInt());
    }

    public static void toBytes(PacketOpenGuiArmor msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeInt(msg.windowID);
    }

    public static void onMessage(PacketOpenGuiArmor msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientOpenGuiHelper.openArmorGUI(msg.windowID, msg.entityID)));
        ctx.get().setPacketHandled(true);
    }
}
