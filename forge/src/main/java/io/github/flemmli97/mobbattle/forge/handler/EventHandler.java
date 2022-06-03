package io.github.flemmli97.mobbattle.forge.handler;

import io.github.flemmli97.mobbattle.forge.Config;
import io.github.flemmli97.mobbattle.handler.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void addTeamTarget(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide && event.getEntity() instanceof Mob) {
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
    public void teamFriendlyFire(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (Utils.isOnSameTeam(event.getEntityLiving(), attacker) && !event.getEntityLiving().getTeam().isAllowFriendlyFire())
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if (event.getEntity() instanceof Mob e) {
            if (e.getTeam() != null) {
                if (Config.clientConf.showTeamParticleTypes.get() && e.level.isClientSide) {
                    DustParticleOptions color = Utils.teamColor.get(e.getTeam().getColor());
                    if (color != null)
                        e.level.addParticle(color, e.getX(), e.getY() + e.getBbHeight() + 0.5, e.getZ(), 0, 0, 0);
                } else if (Config.commonConf.autoAddAI.get() && !e.getTags().contains(LibTags.entityAIAdded)) {
                    Utils.updateEntity(e.getTeam().getName(), e);
                }
            }
        }
    }
}
