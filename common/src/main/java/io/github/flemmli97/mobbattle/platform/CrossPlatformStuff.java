package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.MobBattle;
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

public interface CrossPlatformStuff {

    CrossPlatformStuff INSTANCE = MobBattle.getPlatformInstance(CrossPlatformStuff.class,
            "io.github.flemmli97.mobbattle.fabric.platform.CrossPlatformStuffImpl",
            "io.github.flemmli97.mobbattle.forge.platform.CrossPlatformStuffImpl");

    MenuType<ContainerArmor> getArmorMenuType();

    SimpleRegistryWrapper<MobEffect> registryStatusEffects();

    SimpleRegistryWrapper<EntityType<?>> registryEntities();

    void sendEquipMessage(ItemStack stack, int entityId, int slot);

    void openGuiArmor(ServerPlayer sender, Mob entity);

    void itemStackUpdatePacket(CompoundTag tag);

    boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living);

    GoalSelector goalSelectorFrom(Mob mob, boolean target);
}
