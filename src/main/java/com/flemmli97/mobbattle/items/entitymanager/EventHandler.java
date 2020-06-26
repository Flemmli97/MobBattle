package com.flemmli97.mobbattle.items.entitymanager;

import com.flemmli97.mobbattle.Config;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void addTeamTarget(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof CreatureEntity) {
            if (event.getEntity() instanceof VexEntity) {
                VexEntity vex = (VexEntity) event.getEntity();
                if (vex.getOwner() != null && vex.getOwner().getTeam() != null) {
                    Team.addEntityToTeam(vex, vex.getOwner().getTeam().getName());
                }
            }
            if (event.getEntity().getTeam() != null)
                Team.updateEntity(event.getEntity().getTeam().getName(), (CreatureEntity) event.getEntity());
            if (event.getEntity().getTags().contains("PickUp"))
                ((CreatureEntity) event.getEntity()).goalSelector.addGoal(10, new EntityAIItemPickup((CreatureEntity) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void teamFriendlyFire(LivingAttackEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getEntity().getTeam() != null) {
            LivingEntity ent = (LivingEntity) event.getEntity();
            if (event.getSource().getTrueSource() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
                if (Team.isOnSameTeam(ent, attacker) && !ent.getTeam().getAllowFriendlyFire())
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if (event.getEntity() instanceof CreatureEntity) {
            CreatureEntity e = (CreatureEntity) event.getEntity();
            if (e.getTeam() != null) {
                if (Config.clientConf.showTeamParticleTypes.get() && e.world.isRemote) {
                    RedstoneParticleData color = Team.teamColor.get(e.getTeam().getColor());
                    if (color != null)
                        e.world.addParticle(color, e.getPosX(), e.getPosY() + e.getHeight() + 0.5, e.getPosZ(), 0, 0, 0);
                } else if (Config.commonConf.autoAddAI.get() && !e.getTags().contains("mobbattle:AddedAI")) {
                    Team.updateEntity(e.getTeam().getName(), e);
                }
            }
        }
    }
}
