package io.github.flemmli97.mobbattle.neoforge.handler;

import io.github.flemmli97.mobbattle.common.EventCalls;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class EventHandler {

    @SubscribeEvent
    public void addTeamTarget(EntityJoinLevelEvent event) {
        EventCalls.handleJoinLevel(event.getEntity());
    }

    @SubscribeEvent
    public void teamFriendlyFire(LivingIncomingDamageEvent event) {
        if (!EventCalls.handleFriendlyFire(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    /**
     * Vanilla sets it in Mob#doHurtTarget but all mobs that override that method dont so...
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void setHurtMob(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Mob mob)
            mob.setLastHurtMob(event.getEntity());
    }

    @SubscribeEvent
    public void livingTick(EntityTickEvent.Post event) {
        EventCalls.tick(event.getEntity());
    }

    @SubscribeEvent
    public void levelTick(LevelTickEvent.Post event) {
        if (!event.getLevel().isClientSide() && event.getLevel().dimension().equals(Level.OVERWORLD)) {
            EventCalls.levelTick(event.getLevel());
        }
    }

    @SubscribeEvent
    public void trackingStart(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity living && !living.level().isClientSide()) {
            EventCalls.onStartTracking((ServerPlayer) event.getEntity(), living);
        }
    }

    @SubscribeEvent
    public void trackingStop(PlayerEvent.StopTracking event) {
        if (event.getTarget() instanceof LivingEntity living && !living.level().isClientSide()) {
            EventCalls.onStopTracking((ServerPlayer) event.getEntity(), living);
        }
    }
}
