package io.github.flemmli97.mobbattle.forge;

import io.github.flemmli97.mobbattle.SimpleRegistryWrapper;
import io.github.flemmli97.mobbattle.forge.network.EquipMessage;
import io.github.flemmli97.mobbattle.forge.network.ItemStackUpdate;
import io.github.flemmli97.mobbattle.forge.network.PacketHandler;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class CrossPlatformStuffImpl {

    public static MenuType<ContainerArmor> getArmorMenuType() {
        return ModMenuType.armorMenu.get();
    }

    public static SimpleRegistryWrapper<MobEffect> registryStatusEffects() {
        return new ForgeRegistryWrapper<>(ForgeRegistries.MOB_EFFECTS);
    }

    public static SimpleRegistryWrapper<EntityType<?>> registryEntities() {
        return new ForgeRegistryWrapper<>(ForgeRegistries.ENTITIES);
    }

    public static void sendEquipMessage(ItemStack stack, int entityId, int slot) {
        PacketHandler.sendToServer(new EquipMessage(stack, entityId, slot));
    }

    public static void openGuiArmor(ServerPlayer player, Mob living) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return living.getName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory arg, Player arg2) {
                return new ContainerArmor(i, arg, living);
            }
        }, buf -> buf.writeInt(living.getId()));
    }

    public static void itemStackUpdatePacket(CompoundTag tag) {
        PacketHandler.sendToServer(new ItemStackUpdate(tag));
    }

    public static boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living) {
        return stack.canEquip(slot, living);
    }

    public static GoalSelector goalSelectorFrom(Mob mob, boolean target) {
        return target ? mob.targetSelector : mob.goalSelector;
    }
}
