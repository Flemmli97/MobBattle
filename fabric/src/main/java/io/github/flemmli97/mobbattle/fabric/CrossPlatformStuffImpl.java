package io.github.flemmli97.mobbattle.fabric;

import io.github.flemmli97.mobbattle.SimpleRegistryWrapper;
import io.github.flemmli97.mobbattle.fabric.mixin.MobAccessor;
import io.github.flemmli97.mobbattle.fabric.network.PacketID;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public class CrossPlatformStuffImpl {

    public static MenuType<ContainerArmor> getArmorMenuType() {
        return ModMenuType.armorMenu;
    }

    public static SimpleRegistryWrapper<MobEffect> registryStatusEffects() {
        return new FabricRegistryWrapper<>(Registry.MOB_EFFECT);
    }

    public static SimpleRegistryWrapper<EntityType<?>> registryEntities() {
        return new FabricRegistryWrapper<>(Registry.ENTITY_TYPE);
    }

    public static void sendEquipMessage(ItemStack stack, int entityId, int slot) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        CompoundTag compound = new CompoundTag();
        CompoundTag tag = new CompoundTag();
        compound.putInt("EntityID", entityId);
        if (!stack.isEmpty())
            compound.put("Stack", stack.save(tag));
        compound.putInt("Slot", slot);
        buf.writeNbt(compound);
        ClientPlayNetworking.send(PacketID.equipMessage, buf);
    }

    public static void openGuiArmor(ServerPlayer player, Mob living) {
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

    public static void itemStackUpdatePacket(CompoundTag tag) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(tag);
        ClientPlayNetworking.send(PacketID.effectMessage, buf);
    }

    public static GoalSelector goalSelectorFrom(Mob mob, boolean target) {
        MobAccessor acc = (MobAccessor) mob;
        return target ? acc.getTargetSelector() : acc.getGoalSelector();
    }
}
