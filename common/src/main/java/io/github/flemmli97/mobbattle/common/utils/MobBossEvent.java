package io.github.flemmli97.mobbattle.common.utils;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class MobBossEvent extends ServerBossEvent {

    public static final Codec<MobBossEvent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(UUIDUtil.CODEC.fieldOf("Id").forGetter(BossEvent::getId),
                            ComponentSerialization.CODEC.fieldOf("Name").forGetter(BossEvent::getName),
                            BossBarColor.CODEC.fieldOf("Color").forGetter(BossEvent::getColor)
                    )
                    .apply(instance, MobBossEvent::new)
    );

    private LivingEntity entity;
    private boolean removed;

    public MobBossEvent(LivingEntity entity) {
        super(entity.getUUID(), entity.getDisplayName(), BossBarColor.WHITE, BossBarOverlay.PROGRESS);
        this.entity = entity;
        CrossPlatformStuff.INSTANCE.getTrackingPlayers(entity)
                .forEach(this::addPlayer);
    }

    private MobBossEvent(UUID id, Component name) {
        super(id, name, BossBarColor.WHITE, BossBarOverlay.PROGRESS);
    }

    private MobBossEvent(UUID id, Component name, BossBarColor color) {
        super(id, name, color, BossBarOverlay.PROGRESS);
    }

    public void setEntity(LivingEntity entity) {
        if (entity.getUUID().equals(this.getId())) {
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
}
