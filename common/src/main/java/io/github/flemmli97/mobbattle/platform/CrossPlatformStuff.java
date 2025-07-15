package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.common.components.EffectComponent;
import io.github.flemmli97.mobbattle.common.components.SpawnEggOptions;
import io.github.flemmli97.mobbattle.common.components.UuidComponent;
import io.github.flemmli97.mobbattle.common.components.UuidListComponent;
import io.github.flemmli97.mobbattle.common.inv.ContainerArmor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public interface CrossPlatformStuff {

    CrossPlatformStuff INSTANCE = MobBattle.getPlatformInstance(CrossPlatformStuff.class,
            "io.github.flemmli97.mobbattle.fabric.platform.CrossPlatformStuffImpl",
            "io.github.flemmli97.mobbattle.neoforge.platform.CrossPlatformStuffImpl");

    MenuType<ContainerArmor> getArmorMenuType();

    DataComponentType<UuidComponent> getComponentMobUuid();

    DataComponentType<UuidListComponent> getComponentMobGroupUuid();

    DataComponentType<EffectComponent> getComponentEffect();

    DataComponentType<AreaPositionComponent> getComponentAreaSelection();

    DataComponentType<SpawnEggOptions> getComponentSpawnEggOptions();

    default LivingEntity tryGetEntity(Entity entity) {
        if (entity instanceof EnderDragonPart part) {
            return part.parentMob;
        }
        return entity instanceof LivingEntity mob ? mob : null;
    }

    void openGuiArmor(ServerPlayer sender, Mob entity);

    boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living);

    GoalSelector goalSelectorFrom(Mob mob, boolean target);

    void sendToClient(CustomPacketPayload packet, ServerPlayer player);

    void sendToServer(CustomPacketPayload packet);
}
