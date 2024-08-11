package io.github.flemmli97.mobbattle.forge.handler;

import io.github.flemmli97.mobbattle.forge.Config;
import io.github.flemmli97.mobbattle.handler.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class EventHandler {

    @SubscribeEvent
    public void addTeamTarget(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Mob) {
            if (event.getEntity() instanceof Vex vex) {
                if (vex.getOwner() != null && vex.getOwner().getTeam() != null) {
                    Utils.addEntityToTeam(vex, vex.getOwner().getTeam().getName());
                }
            }
            if (event.getEntity().getTeam() != null)
                Utils.updateEntity(event.getEntity().getTeam().getName(), (Mob) event.getEntity());
            if (event.getEntity().getTags().contains(LibTags.entityPickup))
                ((Mob) event.getEntity()).goalSelector.addGoal(10, new EntityAIItemPickup((Mob) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void teamFriendlyFire(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (Utils.isOnSameTeam(event.getEntity(), attacker) && !event.getEntity().getTeam().isAllowFriendlyFire())
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
        if (event.getEntity() instanceof Mob e) {
            if (e.getTeam() != null) {
                if (Config.CLIENT_CONF.showTeamParticleTypes.get() && e.level().isClientSide) {
                    DustParticleOptions color = Utils.teamColor.get(e.getTeam().getColor());
                    if (color != null)
                        e.level().addParticle(color, e.getX(), e.getY() + e.getBbHeight() + 0.5, e.getZ(), 0, 0, 0);
                } else if (Config.COMMON_CONF.autoAddAI.get() && !e.getTags().contains(LibTags.entityAIAdded)) {
                    Utils.updateEntity(e.getTeam().getName(), e);
                }
            }
        }
    }
}
