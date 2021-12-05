package io.github.flemmli97.mobbattle;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class CrossPlatformStuff {

    @ExpectPlatform
    public static MenuType<ContainerArmor> getArmorMenuType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static SimpleRegistryWrapper<MobEffect> registryStatusEffects() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static SimpleRegistryWrapper<EntityType<?>> registryEntities() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendEquipMessage(ItemStack stack, int entityId, int slot) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void openGuiArmor(ServerPlayer sender, Mob entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void itemStackUpdatePacket(CompoundTag tag) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static GoalSelector goalSelectorFrom(Mob mob, boolean target) {
        throw new AssertionError();
    }
}
