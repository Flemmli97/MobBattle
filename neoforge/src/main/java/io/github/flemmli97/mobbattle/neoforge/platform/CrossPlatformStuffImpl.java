package io.github.flemmli97.mobbattle.neoforge.platform;

import io.github.flemmli97.mobbattle.components.AreaPositionComponent;
import io.github.flemmli97.mobbattle.components.EffectComponent;
import io.github.flemmli97.mobbattle.components.UuidComponent;
import io.github.flemmli97.mobbattle.components.UuidListComponent;
import io.github.flemmli97.mobbattle.inv.ContainerArmor;
import io.github.flemmli97.mobbattle.neoforge.client.ClientEvents;
import io.github.flemmli97.mobbattle.neoforge.registry.ModComponents;
import io.github.flemmli97.mobbattle.neoforge.registry.ModMenuType;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
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
        return ModMenuType.ARMOR_MENU.get();
    }

    @Override
    public DataComponentType<UuidComponent> getComponentMobUuid() {
        return ModComponents.SELECTED_MOB.get();
    }

    @Override
    public DataComponentType<UuidListComponent> getComponentMobGroupUuid() {
        return ModComponents.SELECTED_MOBS.get();
    }

    @Override
    public DataComponentType<EffectComponent> getComponentEffect() {
        return ModComponents.EFFECT.get();
    }

    @Override
    public DataComponentType<AreaPositionComponent> getComponentAreaSelection() {
        return ModComponents.BOX.get();
    }

    @Override
    public void openGuiArmor(ServerPlayer player, Mob living) {
        player.openMenu(new MenuProvider() {
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

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living) {
        return stack.canEquip(slot, living);
    }

    @Override
    public GoalSelector goalSelectorFrom(Mob mob, boolean target) {
        return target ? mob.targetSelector : mob.goalSelector;
    }

    @Override
    public void sendToClient(CustomPacketPayload packet, ServerPlayer player) {
        player.connection.send(packet);
    }

    @Override
    public void sendToServer(CustomPacketPayload packet) {
        ClientEvents.sendPacketServer(packet);
    }
}
