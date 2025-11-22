package io.github.flemmli97.mobbattle.platform;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface CrossPlatformStuff {

    TagKey<EntityType<?>> MULTIPART_ENTITY = TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath("c", "multipart_entity"));

    CrossPlatformStuff INSTANCE = MobBattle.getPlatformInstance(CrossPlatformStuff.class,
            "io.github.flemmli97.mobbattle.fabric.platform.CrossPlatformStuffImpl",
            "io.github.flemmli97.mobbattle.neoforge.platform.CrossPlatformStuffImpl");

    <T extends Item> Supplier<T> registerItem(String id, Supplier<T> sup);

    <T> Supplier<DataComponentType<T>> registerComponent(String id, Supplier<DataComponentType<T>> sup);

    <T extends AbstractContainerMenu, D> Supplier<MenuType<T>> registerMenu(String id, MenuFactory<T, D> factory, StreamCodec<RegistryFriendlyByteBuf, D> codec);

    default Entity tryGetEntity(Entity entity) {
        if (entity instanceof OwnableEntity ownable && entity.getType().is(MULTIPART_ENTITY))
            return ownable.getOwner();
        if (entity instanceof TraceableEntity traceableEntity && entity.getType().is(MULTIPART_ENTITY) && traceableEntity.getOwner() instanceof LivingEntity owner)
            return owner;
        if (entity instanceof EnderDragonPart part)
            return part.parentMob;
        return entity;
    }

    default LivingEntity tryGetLivingEntity(Entity entity) {
        return this.tryGetEntity(entity) instanceof LivingEntity living ? living : null;
    }

    void openGuiArmor(ServerPlayer sender, Mob entity);

    boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity living);

    GoalSelector goalSelectorFrom(Mob mob, boolean target);

    void sendToClient(CustomPacketPayload packet, ServerPlayer player);

    void sendToServer(CustomPacketPayload packet);

    interface MenuFactory<T extends AbstractContainerMenu, D> {
        T create(int idx, Inventory inv, D data);
    }
}
