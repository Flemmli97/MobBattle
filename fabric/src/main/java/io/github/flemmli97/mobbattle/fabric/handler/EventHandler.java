package io.github.flemmli97.mobbattle.fabric.handler;

import io.github.flemmli97.mobbattle.fabric.Config;
import io.github.flemmli97.mobbattle.fabric.mixin.MobAccessor;
import io.github.flemmli97.mobbattle.handler.EntityAIItemPickup;
import io.github.flemmli97.mobbattle.handler.LibTags;
import io.github.flemmli97.mobbattle.handler.Utils;
import io.github.flemmli97.mobbattle.items.LeftClickInteractItem;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class EventHandler {

    public static InteractionResult attackCallback(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof LeftClickInteractItem) {
            if (((LeftClickInteractItem) stack.getItem()).onLeftClickEntity(stack, player, entity))
                return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void addTeamTarget(Entity entity) {
        if (!entity.level.isClientSide && entity instanceof Mob) {
            if (entity instanceof Vex) {
                Vex vex = (Vex) entity;
                if (vex.getOwner() != null && vex.getOwner().getTeam() != null) {
                    Utils.addEntityToTeam(vex, vex.getOwner().getTeam().getName());
                }
            }
            if (entity.getTeam() != null)
                Utils.updateEntity(entity.getTeam().getName(), (Mob) entity);
            if (entity.getTags().contains(LibTags.entityPickup))
                ((MobAccessor) entity).getGoalSelector().addGoal(10, new EntityAIItemPickup((Mob) entity));
        }
    }

    public static boolean teamFriendlyFire(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getTeam() != null) {
            if (source.getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) source.getEntity();
                if (Utils.isOnSameTeam(entity, attacker) && !entity.getTeam().isAllowFriendlyFire())
                    return false;
            }
        }
        return true;
    }

    public static void livingTick(LivingEntity entity) {
        if (entity instanceof Mob) {
            Mob e = (Mob) entity;
            if (e.getTeam() != null) {
                if (Config.config.showTeamParticleTypes && e.level.isClientSide) {
                    DustParticleOptions color = Utils.teamColor.get(e.getTeam().getColor());
                    if (color != null)
                        e.level.addParticle(color, e.getX(), e.getY() + e.getBbHeight() + 0.5, e.getZ(), 0, 0, 0);
                } else if (Config.config.autoAddAI && !e.getTags().contains(LibTags.entityAIAdded)) {
                    Utils.updateEntity(e.getTeam().getName(), e);
                }
            }
        }
    }
}
