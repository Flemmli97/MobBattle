package io.github.flemmli97.mobbattle.common;

import io.github.flemmli97.mobbattle.common.entity.ai.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.common.utils.BossbarHandler;
import io.github.flemmli97.mobbattle.common.utils.LibTags;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.mixin.MobAccessor;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;

public class EventCalls {

    public static void handleJoinLevel(Entity entity) {
        if (!entity.level().isClientSide && entity instanceof Mob mob) {
            if (entity instanceof TraceableEntity traceable) {
                Entity owner = traceable.getOwner();
                if (owner != null && owner.getTeam() != null) {
                    Utils.addEntityToTeam(entity, owner.getTeam().getName());
                }
            } else if (entity instanceof OwnableEntity ownable) {
                Entity owner = ownable.getOwner();
                if (owner != null && owner.getTeam() != null) {
                    Utils.addEntityToTeam(entity, owner.getTeam().getName());
                }
            }
            if (entity.getTeam() != null)
                Utils.updateEntity(entity.getTeam().getName(), mob);
            if (entity.getTags().contains(LibTags.ENTITY_PICKUP)) {
                ((MobAccessor) mob).getGoalSelector().addGoal(10, new EntityAIItemPickup(mob));
            }
        }
        if (!entity.level().isClientSide && entity instanceof LivingEntity living) {
            BossbarHandler.get(living.level().getServer()).onMobLoad(living);
        }
    }

    public static boolean handleFriendlyFire(LivingEntity entity, DamageSource source) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            return !Utils.isOnSameTeam(entity, attacker) || entity.getTeam().isAllowFriendlyFire();
        }
        return true;
    }

    public static void tick(Entity entity) {
        if (entity instanceof Mob mob) {
            if (mob.getTeam() != null) {
                if (Config.showTeamParticleTypes && mob.level().isClientSide) {
                    DustParticleOptions color = Utils.teamColor.get(mob.getTeam().getColor());
                    if (color != null)
                        mob.level().addParticle(color, mob.getX(), mob.getY() + mob.getBbHeight() + 0.5, mob.getZ(), 0, 0, 0);
                } else if (Config.autoAddAI && !mob.getTags().contains(LibTags.ENTITY_AI_ADDED)) {
                    Utils.updateEntity(mob.getTeam().getName(), mob);
                }
            }
        }
    }

    public static void levelTick(Level level) {
        if (level.getServer() == null)
            return;
        BossbarHandler.get(level.getServer()).tick();
    }

    public static void onStartTracking(ServerPlayer player, LivingEntity target) {
        BossbarHandler.get(player.getServer()).onStartTracking(player, target);
    }

    public static void onStopTracking(ServerPlayer player, LivingEntity target) {
        BossbarHandler.get(player.getServer()).onStopTracking(player, target);
    }
}
