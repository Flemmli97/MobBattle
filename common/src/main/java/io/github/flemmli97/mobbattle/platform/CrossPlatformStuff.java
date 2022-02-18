package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.SimpleRegistryWrapper;
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

public abstract class CrossPlatformStuff {

    protected static CrossPlatformStuff INSTANCE;

    public static CrossPlatformStuff instance() {
        return INSTANCE;
    }

    public abstract MenuType<ContainerArmor> getArmorMenuType();

    public abstract SimpleRegistryWrapper<MobEffect> registryStatusEffects();

    public abstract SimpleRegistryWrapper<EntityType<?>> registryEntities();

    public abstract void sendEquipMessage(ItemStack stack, int entityId, int slot);

    public abstract void openGuiArmor(ServerPlayer sender, Mob entity);

    public abstract void itemStackUpdatePacket(CompoundTag tag);

    public abstract boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living);

    public abstract GoalSelector goalSelectorFrom(Mob mob, boolean target);
}
