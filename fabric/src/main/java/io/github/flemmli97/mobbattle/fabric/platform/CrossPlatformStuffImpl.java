package io.github.flemmli97.mobbattle.fabric.platform;

import com.google.common.collect.ImmutableList;
import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.inv.ContainerArmor;
import io.github.flemmli97.mobbattle.mixin.MobAccessor;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CrossPlatformStuffImpl implements CrossPlatformStuff {

    private static final List<Item> ITEMS = new ArrayList<>();

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, @UnknownNullability Function<Identifier, T> func) {
        Identifier itemId = MobBattle.of(id);
        T reg = Registry.register(BuiltInRegistries.ITEM, itemId, func.apply(itemId));
        ITEMS.add(reg);
        return () -> reg;
    }

    public static List<Item> modItems() {
        return ImmutableList.copyOf(ITEMS);
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerComponent(String id, Supplier<DataComponentType<T>> sup) {
        DataComponentType<T> reg = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, MobBattle.of(id), sup.get());
        return () -> reg;
    }

    @Override
    public <T extends AbstractContainerMenu, D> Supplier<MenuType<T>> registerMenu(String id, MenuFactory<T, D> factory, StreamCodec<RegistryFriendlyByteBuf, D> codec) {
        MenuType<T> reg = Registry.register(BuiltInRegistries.MENU, MobBattle.of("id"), new ExtendedMenuType<>(factory::create, codec));
        return () -> reg;
    }

    @Override
    public Collection<ServerPlayer> getTrackingPlayers(Entity entity) {
        return PlayerLookup.tracking(entity);
    }

    @Override
    public void openGuiArmor(ServerPlayer player, Mob living) {
        player.openMenu(new ExtendedMenuProvider<>() {
            @Override
            public Integer getScreenOpeningData(ServerPlayer player) {
                return living.getId();
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
        return slot == living.getEquipmentSlotForItem(stack);
    }

    @Override
    public GoalSelector goalSelectorFrom(Mob mob, boolean target) {
        MobAccessor acc = (MobAccessor) mob;
        return target ? acc.getTargetSelector() : acc.getGoalSelector();
    }

    @Override
    public void sendToClient(CustomPacketPayload packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet);
    }

    @Override
    public void sendToServer(CustomPacketPayload packet) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.send(packet);
        }
    }
}
