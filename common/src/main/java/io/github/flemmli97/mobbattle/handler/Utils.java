package io.github.flemmli97.mobbattle.handler;

import com.mojang.math.Vector3f;
import io.github.flemmli97.mobbattle.platform.CrossPlatformStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team.CollisionRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

    public static Map<ChatFormatting, DustParticleOptions> teamColor = new HashMap<>();

    static {
        teamColor.put(ChatFormatting.AQUA, new DustParticleOptions(new Vector3f(0.01f, 0.9f, 1f), 1.0f));
        teamColor.put(ChatFormatting.BLACK, new DustParticleOptions(new Vector3f(0.01f, 0, 0f), 1.0f));
        teamColor.put(ChatFormatting.BLUE, new DustParticleOptions(new Vector3f(0.2f, 0.2f, 1), 1.0f));
        teamColor.put(ChatFormatting.DARK_AQUA, new DustParticleOptions(new Vector3f(0.01f, 0.4f, 0.5f), 1.0f));
        teamColor.put(ChatFormatting.DARK_BLUE, new DustParticleOptions(new Vector3f(0.01f, 0, 0.4f), 1.0f));
        teamColor.put(ChatFormatting.DARK_GRAY, new DustParticleOptions(new Vector3f(0.2f, 0.2f, 0.2f), 1.0f));
        teamColor.put(ChatFormatting.DARK_GREEN, new DustParticleOptions(new Vector3f(0.01f, 0.5f, 0), 1.0f));
        teamColor.put(ChatFormatting.DARK_PURPLE, new DustParticleOptions(new Vector3f(0.3f, 0, 0.4f), 1.0f));
        teamColor.put(ChatFormatting.DARK_RED, new DustParticleOptions(new Vector3f(0.5f, 0, 0), 1.0f));
        teamColor.put(ChatFormatting.GOLD, new DustParticleOptions(new Vector3f(1, 0.6f, 0), 1.0f));
        teamColor.put(ChatFormatting.GRAY, new DustParticleOptions(new Vector3f(0.4f, 0.4f, 0.4f), 1.0f));
        teamColor.put(ChatFormatting.GREEN, new DustParticleOptions(new Vector3f(0.01f, 1, 0), 1.0f));
        teamColor.put(ChatFormatting.LIGHT_PURPLE, new DustParticleOptions(new Vector3f(0.6f, 0, 0.7f), 1.0f));
        teamColor.put(ChatFormatting.RED, new DustParticleOptions(new Vector3f(1, 0.2f, 0.2f), 1.0f));
        teamColor.put(ChatFormatting.WHITE, new DustParticleOptions(new Vector3f(1, 1, 1), 1.0f));
        teamColor.put(ChatFormatting.YELLOW, new DustParticleOptions(new Vector3f(1, 1, 0), 1.0f));
    }

    public static String getTeam(Entity entity) {
        return entity.getTeam() != null ? entity.getTeam().getName() : "none";
    }

    public static boolean isOnSameTeam(Entity entity, Entity entity2) {
        if (entity.getTeam() == null)
            return true;
        if (entity2.getTeam() == null)
            return true;
        return entity.isAlliedTo(entity2);
    }

    public static void addEntityToTeam(Entity entity, String team) {
        Scoreboard score = entity.level.getScoreboard();
        PlayerTeam scoreTeam = score.getPlayerTeam(team);
        if (scoreTeam == null) {
            scoreTeam = score.addPlayerTeam(team);
            scoreTeam.setCollisionRule(CollisionRule.PUSH_OTHER_TEAMS);
        }
        score.getPlayerTeam(team).getPlayers().size();
        score.addPlayerToTeam(entity.getStringUUID(), scoreTeam);
    }

    public static int getTeamSize(Entity entity, String team) {
        return entity.level.getScoreboard().getPlayerTeam(team) != null ? entity.level.getScoreboard().getPlayerTeam(team).getPlayers().size() : 0;
    }

    private static final Predicate<Goal> targetGoal = (goal) -> true;

    public static void updateEntity(String team, Mob e) {
        addEntityToTeam(e, team);
        removeGoal(CrossPlatformStuff.INSTANCE.goalSelectorFrom(e, true), targetGoal);
        e.setTarget(null);
        CrossPlatformStuff.INSTANCE.goalSelectorFrom(e, true).addGoal(0, new EntityAITeamTarget(e, false, true));
        e.addTag(LibTags.entityAIAdded);
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

    public static Mob fromUUID(ServerLevel world, String uuid) {
        if (uuid != null) {
            Entity e = world.getEntity(UUID.fromString(uuid));
            if (e instanceof Mob)
                return (Mob) e;
        }
        return null;
    }

    public static void setAttackTarget(Mob entity, LivingEntity target, boolean both) {
        if (target == null)
            return;
        entity.setTarget(target);
        entity.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 600);
        entity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, target, 600);
        if (target instanceof Mob && both) {
            ((Mob) target).setTarget(entity);
            target.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, entity.getUUID(), 600);
            target.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, entity, 600);
        }
    }
}
