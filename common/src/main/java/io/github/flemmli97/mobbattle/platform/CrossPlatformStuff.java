package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import io.github.flemmli97.mobbattle.network.Packet;
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
            "io.github.flemmli97.mobbattle.forge.platform.CrossPlatformStuffImpl");

    MenuType<ContainerArmor> getArmorMenuType();

    default LivingEntity tryGetEntity(Entity entity) {
        if (entity instanceof EnderDragonPart part) {
            return part.parentMob;
        }
        return entity instanceof LivingEntity mob ? mob : null;
    }

    void openGuiArmor(ServerPlayer sender, Mob entity);

    boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living);

    GoalSelector goalSelectorFrom(Mob mob, boolean target);

    void sendToClient(Packet packet, ServerPlayer player);

    void sendToServer(Packet packet);
}
