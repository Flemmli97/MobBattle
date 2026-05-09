package io.github.flemmli97.mobbattle.common.utils;

import io.github.flemmli97.mobbattle.MobBattle;
import io.github.flemmli97.mobbattle.common.Config;
import io.github.flemmli97.mobbattle.common.entity.ai.EntityAIHurt;
import io.github.flemmli97.mobbattle.common.entity.ai.EntityAITeamTarget;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

    public static Map<ChatFormatting, DustParticleOptions> teamColor = new HashMap<>();

    public static final Identifier MOB_BATTLE_FOLLOW_MOD = Identifier.fromNamespaceAndPath(MobBattle.MODID, "follow_target_mod");

    static {
        teamColor.put(ChatFormatting.AQUA, new DustParticleOptions(ARGB.colorFromFloat(1, 0.01f, 0.9f, 1f), 1.0f));
        teamColor.put(ChatFormatting.BLACK, new DustParticleOptions(ARGB.colorFromFloat(1, 0.01f, 0, 0f), 1.0f));
        teamColor.put(ChatFormatting.BLUE, new DustParticleOptions(ARGB.colorFromFloat(1, 0.2f, 0.2f, 1), 1.0f));
        teamColor.put(ChatFormatting.DARK_AQUA, new DustParticleOptions(ARGB.colorFromFloat(1, 0.01f, 0.4f, 0.5f), 1.0f));
        teamColor.put(ChatFormatting.DARK_BLUE, new DustParticleOptions(ARGB.colorFromFloat(1, 0.01f, 0, 0.4f), 1.0f));
        teamColor.put(ChatFormatting.DARK_GRAY, new DustParticleOptions(ARGB.colorFromFloat(1, 0.2f, 0.2f, 0.2f), 1.0f));
        teamColor.put(ChatFormatting.DARK_GREEN, new DustParticleOptions(ARGB.colorFromFloat(1, 0.01f, 0.5f, 0), 1.0f));
        teamColor.put(ChatFormatting.DARK_PURPLE, new DustParticleOptions(ARGB.colorFromFloat(1, 0.3f, 0, 0.4f), 1.0f));
        teamColor.put(ChatFormatting.DARK_RED, new DustParticleOptions(ARGB.colorFromFloat(1, 0.5f, 0, 0), 1.0f));
        teamColor.put(ChatFormatting.GOLD, new DustParticleOptions(ARGB.colorFromFloat(1, 1, 0.6f, 0), 1.0f));
        teamColor.put(ChatFormatting.GRAY, new DustParticleOptions(ARGB.colorFromFloat(1, 0.4f, 0.4f, 0.4f), 1.0f));
        teamColor.put(ChatFormatting.GREEN, new DustParticleOptions(ARGB.colorFromFloat(1, 0.01f, 1, 0), 1.0f));
        teamColor.put(ChatFormatting.LIGHT_PURPLE, new DustParticleOptions(ARGB.colorFromFloat(1, 0.6f, 0, 0.7f), 1.0f));
        teamColor.put(ChatFormatting.RED, new DustParticleOptions(ARGB.colorFromFloat(1, 1, 0.2f, 0.2f), 1.0f));
        teamColor.put(ChatFormatting.WHITE, new DustParticleOptions(ARGB.colorFromFloat(1, 1, 1, 1), 1.0f));
        teamColor.put(ChatFormatting.YELLOW, new DustParticleOptions(ARGB.colorFromFloat(1, 1, 1, 0), 1.0f));
    }

    public static boolean isOnSameTeam(Entity entity, Entity entity2) {
        if (entity.getTeam() != null && entity2.getTeam() != null)
            return entity.isAlliedTo(entity2);
        return false;
    }

    public static boolean canTargetEntity(Entity entity, Entity entity2) {
        if (entity.getTeam() == null || entity2.getTeam() == null)
            return false;
        return !entity.isAlliedTo(entity2);
    }

    public static void addEntityToTeam(Entity entity, String team) {
        Scoreboard score = entity.level().getScoreboard();
        PlayerTeam scoreTeam = score.getPlayerTeam(team);
        if (scoreTeam == null) {
            scoreTeam = score.addPlayerTeam(team);
            scoreTeam.setCollisionRule(CollisionRule.PUSH_OTHER_TEAMS);
        }
        score.getPlayerTeam(team).getPlayers().size();
        score.addPlayerToTeam(entity.getStringUUID(), scoreTeam);
    }

    private static final Predicate<Goal> targetGoal = (goal) -> true;

    public static void updateEntity(String team, Mob mob) {
        team = team.replace(" ", "");
        addEntityToTeam(mob, team);
        mob.setTarget(null);
        mob.addTag(LibTags.ENTITY_AI_ADDED);
        if (mob.is(MobBattle.IGNORED))
            return;
        removeGoal(CrossPlatformStuff.INSTANCE.goalSelectorFrom(mob, true), targetGoal);
        increaseFollow(mob);
        CrossPlatformStuff.INSTANCE.goalSelectorFrom(mob, true).addGoal(0, new EntityAIHurt(mob));
        CrossPlatformStuff.INSTANCE.goalSelectorFrom(mob, true).addGoal(3, new EntityAITeamTarget(mob, false, true));
    }

    /**
     * like {@link GoalSelector#removeGoal(Goal)} but with a predicate
     */
    private static void removeGoal(GoalSelector goalSel, Predicate<Goal> pred) {
        Set<WrappedGoal> goals = goalSel.getAvailableGoals()
                .stream().filter(prio -> pred.test(prio.getGoal())).collect(Collectors.toSet());
        goals.forEach(goalSel::removeGoal);
    }

    public static AABB getBoundingBoxPositions(BlockPos pos, BlockPos pos2) {
        if (pos2 == null) {
            return new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).move(pos);
        } else {
            int xDiff = pos.getX() - pos2.getX();
            int yDiff = pos.getY() - pos2.getY();
            int zDiff = pos.getZ() - pos2.getZ();
            int x, y, z, x2, y2, z2;
            x = y = z = x2 = y2 = z2 = 0;
            if (xDiff <= 0)
                x = 1;
            else
                x2 = 1;
            if (yDiff <= 0)
                y = 1;
            else
                y2 = 1;
            if (zDiff <= 0)
                z = 1;
            else
                z2 = 1;
            return new AABB(x, y, z, xDiff + x2, yDiff + y2, zDiff + z2).move(pos2);
        }
    }

    public static Mob fromUUID(ServerLevel level, UUID uuid) {
        if (uuid != null) {
            Entity e = level.getEntity(uuid);
            if (e instanceof Mob)
                return (Mob) e;
        }
        return null;
    }

    public static void setAttackTarget(Mob entity, LivingEntity target, boolean both) {
        if (target == null)
            return;
        setTargetTo(entity, target);
        increaseFollow(entity);
        if (target instanceof Mob mobTarget && both) {
            increaseFollow(mobTarget);
            setTargetTo(mobTarget, entity);
        }
    }

    private static void increaseFollow(Mob mob) {
        AttributeInstance att = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (Config.followRangeIncrease > 0 && att != null && !att.hasModifier(MOB_BATTLE_FOLLOW_MOD)) {
            att.addTransientModifier(new AttributeModifier(MOB_BATTLE_FOLLOW_MOD, Config.followRangeIncrease, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void setTargetTo(Mob entity, LivingEntity target) {
        entity.setTarget(target);
        entity.getBrain().setMemory(MemoryModuleType.ANGRY_AT, target.getUUID());
        entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        if (entity instanceof Warden warden) {
            warden.increaseAngerAt(target, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            warden.setAttackTarget(target);
        }
        if (entity instanceof ActiveTargetMobbattle act)
            act.mobbattle$setTargeting(true);
    }

    public static void handleTeamKill(Scoreboard scoreboard, ScoreHolder scoreHolder, ScoreHolder teamMember, ObjectiveCriteria[] crtieria) {
        int i;
        PlayerTeam playerTeam = scoreboard.getPlayersTeam(teamMember.getScoreboardName());
        if (playerTeam != null && (i = playerTeam.getColor().getId()) >= 0 && i < crtieria.length) {
            scoreboard.forAllObjectives(crtieria[i], scoreHolder, ScoreAccess::increment);
        }
    }
}
