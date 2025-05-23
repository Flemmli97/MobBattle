package io.github.flemmli97.mobbattle.fabric.platform;

import io.github.flemmli97.mobbattle.fabric.mixin.MobAccessor;
import io.github.flemmli97.mobbattle.fabric.registry.ModMenuType;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import io.github.flemmli97.mobbattle.network.Packet;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public class CrossPlatformStuffImpl implements CrossPlatformStuff {

    @Override
    public MenuType<ContainerArmor> getArmorMenuType() {
        return ModMenuType.armorMenu;
    }

    @Override
    public void openGuiArmor(ServerPlayer player, Mob living) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeInt(living.getId());
            }

            @Override
            public Component getDisplayName() {
                return living.getName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory arg, Player arg2) {
                return new ContainerArmor(i, arg, living);
            }
        });
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living) {
        return slot == Mob.getEquipmentSlotForItem(stack);
    }

    @Override
    public GoalSelector goalSelectorFrom(Mob mob, boolean target) {
        MobAccessor acc = (MobAccessor) mob;
        return target ? acc.getTargetSelector() : acc.getGoalSelector();
    }

    @Override
    public void sendToClient(Packet packet, ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        ServerPlayNetworking.send(player, packet.getID(), buf);
    }

    @Override
    public void sendToServer(Packet packet) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            packet.write(buf);
            ClientPlayNetworking.send(packet.getID(), buf);
        }
    }
}
