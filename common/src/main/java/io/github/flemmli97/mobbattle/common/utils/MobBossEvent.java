package io.github.flemmli97.mobbattle.common.utils;

import com.google.common.base.Objects;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class MobBossEvent extends ServerBossEvent {

    private final UUID id;
    private LivingEntity entity;
    private boolean removed;

    public MobBossEvent(LivingEntity entity) {
        super(entity.getDisplayName(), BossBarColor.WHITE, BossBarOverlay.PROGRESS);
        this.id = entity.getUUID();
        this.entity = entity;
        CrossPlatformStuff.INSTANCE.getTrackingPlayers(entity)
                .forEach(this::addPlayer);
    }

    private MobBossEvent(UUID id, Component name) {
        super(name, BossBarColor.WHITE, BossBarOverlay.PROGRESS);
        this.id = id;
    }

    public UUID id() {
        return this.id;
    }

    public void setEntity(LivingEntity entity) {
        if (entity.getUUID().equals(this.id())) {
            this.entity = entity;
            this.setName(entity.getDisplayName());
            this.removeAllPlayers();
            CrossPlatformStuff.INSTANCE.getTrackingPlayers(entity)
                    .forEach(this::addPlayer);
        }
    }

    public void remove() {
        this.removed = true;
        this.removeAllPlayers();
    }

    public boolean tick() {
        if (this.removed) {
            return true;
        }
        if (this.entity != null) {
            if (this.entity.isRemoved()) {
                if (this.entity.getRemovalReason() == Entity.RemovalReason.DISCARDED || this.entity.getRemovalReason() == Entity.RemovalReason.KILLED) {
                    this.removeAllPlayers();
                    return true;
                }
                this.entity = null;
                return false;
            }
            this.setProgress(this.entity.getHealth() / this.entity.getMaxHealth());
            Component displayName = this.entity.getDisplayName();
            if (!Objects.equal(displayName, this.getName())) {
                this.setName(displayName);
            }
        }
        return false;
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Color", this.getColor().getName());
        tag.put("Name", ComponentSerialization.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.getName()).getOrThrow());
        tag.put("Id", UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, this.id()).getOrThrow());
        return tag;
    }

    public static MobBossEvent load(CompoundTag tag, HolderLookup.Provider provider) {
        UUID id = UUIDUtil.CODEC.parse(NbtOps.INSTANCE, tag.get("Id")).getOrThrow();
        Component name = ComponentSerialization.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("Name")).getOrThrow();
        MobBossEvent event = new MobBossEvent(id, name);
        event.setColor(BossBarColor.byName(tag.getString("Color")));
        return event;
    }
}
