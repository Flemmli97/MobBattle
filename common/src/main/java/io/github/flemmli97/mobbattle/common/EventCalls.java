package io.github.flemmli97.mobbattle.common;

import io.github.flemmli97.mobbattle.common.entity.goal.ItemPickupGoal;
import io.github.flemmli97.mobbattle.common.entity.goal.PerimeterGoal;
import io.github.flemmli97.mobbattle.common.registry.MobBattleItems;
import io.github.flemmli97.mobbattle.common.utils.BossbarHandler;
import io.github.flemmli97.mobbattle.common.utils.LibTags;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import io.github.flemmli97.mobbattle.mixin.MobAccessor;
import io.github.flemmli97.mobbattle.network.S2CPerimeterInfo;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EventCalls {

    public static void handleJoinLevel(Entity entity) {
        if (!entity.level().isClientSide() && entity instanceof Mob mob) {
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
            if (entity.entityTags().contains(LibTags.ENTITY_PICKUP)) {
                ((MobAccessor) mob).getGoalSelector().addGoal(10, new ItemPickupGoal(mob));
            }
            ((MobAccessor) mob).getGoalSelector().addGoal(-1, new PerimeterGoal(mob));
        }
        if (!entity.level().isClientSide() && entity instanceof LivingEntity living) {
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
                if (Config.showTeamParticleTypes && mob.level().isClientSide()) {
                    DustParticleOptions color = Utils.teamColor.get(mob.getTeam().getColor());
                    if (color != null)
                        mob.level().addParticle(color, mob.getX(), mob.getY() + mob.getBbHeight() + 0.5, mob.getZ(), 0, 0, 0);
                } else if (Config.autoAddAI && !mob.entityTags().contains(LibTags.ENTITY_AI_ADDED)) {
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
        BossbarHandler.get(player.level().getServer()).onStartTracking(player, target);
    }

    public static void onStopTracking(ServerPlayer player, LivingEntity target) {
        BossbarHandler.get(player.level().getServer()).onStopTracking(player, target);
    }

    public static void onEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        if (entity instanceof ServerPlayer player && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
                && stack.is(MobBattleItems.PERIMETER_TOOL.get())) {
            CrossPlatformStuff.INSTANCE.sendToClient(new S2CPerimeterInfo(player.level()), player);
        }
    }
}
